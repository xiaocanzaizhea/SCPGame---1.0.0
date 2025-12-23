package xyz.xiaocan.scpitemstacks.weapon.gun.absClass;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.configload.option.itemoption.gun.Gun;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.*;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.RoleType;
import xyz.xiaocan.tools.progressBar;
import xyz.xiaocan.tools.util;

import java.util.*;

@Getter
@Setter
public abstract class GunSCPItem extends AbstractSCPItem implements
        IOnRightClick, IOnLeftClick, IOnSwapHandClick, IOnPlayerDrop, IOnSwitchItemBar {

    // ---------- 需要配置的数据-------------
    protected double damage;
    protected double reloadTime;
    protected AmmoType ammoType;
    protected double rateOfFire;
    protected int maxAmmo;
    protected double aimingAccuracy;
    protected double waistShootAccuracy;
    protected String fireSound;
    protected String equipSound;

    //-------------不需要配置的数据---------------
    protected int currentAmmo;
    protected long lastShootTime;
    protected boolean isSetUpGun;
    protected ItemStack crossBow;

    protected BukkitTask reloadTask;
    private float reloadTaskTime;

    public GunSCPItem(Gun gun) {
        super(gun.getGunType().getId() + UUID.randomUUID(), gun.disPlayName, gun.material, gun.customModelData, gun.lore);
        this.damage = gun.damage;
        this.reloadTime = gun.reloadTime;
        this.ammoType = gun.ammoType;
        this.rateOfFire = gun.rateOfFire;
        this.maxAmmo = gun.maxAmmo;
        this.aimingAccuracy = gun.aimingAccuracy;
        this.waistShootAccuracy = gun.waistShootAccuracy;

        this.currentAmmo = maxAmmo-1;
        this.lastShootTime = -1;
        this.isSetUpGun = false;
        this.crossBow = createItemStack();
        this.fireSound = gun.fireSound;
        this.equipSound = gun.equipSound;
        this.reloadTaskTime = 0;
    }

    @Override
    public ItemStack createItemStack(){
        ItemStack itemStack = super.createItemStack();
        itemStack = preloadArrow(itemStack);
        return itemStack;
    }
    public ItemStack preloadArrow(ItemStack crossbow) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();

        AmmoType ammoType1 = ammoType;

        arrowMeta.setDisplayName("§b" + ItemManager.getInstance().getAllAmmos()
                .get(ammoType1).getDisplayName());
        arrow.setItemMeta(arrowMeta);

        CrossbowMeta crossbowMeta = (CrossbowMeta) crossbow.getItemMeta();

        crossbowMeta.addChargedProjectile(arrow);

        crossbow.setItemMeta(crossbowMeta);
        return crossbow;
    }
    protected void setAmmo(int ammo){
        currentAmmo = Math.max(0, ammo);//防止小于0
    }
    protected void reload(Player player){
        ItemManager itemManager = ItemManager.getInstance();
        Map<AmmoType, Integer> ammoTypeIntegerMap = itemManager
                .allPlayersAmmo.get(player.getUniqueId());
        int totalAmmo = ammoTypeIntegerMap.get(ammoType);

        int reloadAmmo = Math.min(totalAmmo,maxAmmo);
        ammoTypeIntegerMap.put(ammoType, totalAmmo - reloadAmmo);  //减少弹药
        currentAmmo = reloadAmmo;
        itemManager.updatePlayerInventoryAmmo(player);  //更新可视化
    }
    protected double getAccuracy(){
        if(isSetUpGun){
            return aimingAccuracy;
        }else {
            return waistShootAccuracy;
        }
    }
    protected ItemStack getCurrentStateItem(){
        ItemStack result = crossBow.clone();
        if(isSetUpGun){
            return result = preloadArrow(result);
        }else{
            return result;
        }
    }
    public void onSwapHandClick(PlayerSwapHandItemsEvent event) {
        if(currentAmmo == maxAmmo) return;
        if(reloadTask != null && !reloadTask.isCancelled()) return; //正在换弹

        Player player = event.getPlayer();

        Map<AmmoType, Integer> ammoTypeIntegerMap = ItemManager.getInstance()
                .allPlayersAmmo.get(player.getUniqueId());

        int currentInventoryAmmo = ammoTypeIntegerMap.getOrDefault(ammoType, 0);
        int ammoToReturn = currentAmmo;
        ammoTypeIntegerMap.put(ammoType, currentInventoryAmmo + ammoToReturn);
        currentAmmo = 0;

        reloadTask = new BukkitRunnable(){
            float progress = reloadTaskTime;

            @Override
            public void run() {
                progress += 0.05;

                if(progress >= reloadTime){
                    reload(player);
                    cancelGunReloadTask(player);
                    reloadTaskTime = 0; // 完成任务后重置

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                    return;
                }

                reloadTaskTime = progress;

                progressBar.updateUseProgress(player,
                        progress, (float) reloadTime,
                        ChatColor.GREEN + disPlayName + "装弹进度");
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);

        event.setCancelled(true);
    }
    @Override
    public void OnLeftClick(PlayerInteractEvent event) { //瞄准逻辑 左键点击
        Player player = event.getPlayer();
        isSetUpGun = !isSetUpGun;
        player.getInventory().setItemInMainHand(getCurrentStateItem());
        player.updateInventory();

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
    }
    @Override
    public void onPlayerDrop(PlayerDropItemEvent event) {
        cancelGunReloadTask(event.getPlayer());
    }
    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){ //playsound
            event.getPlayer().getWorld().playSound(
                    event.getPlayer().getLocation(),
                    String.valueOf(NamespacedKey.fromString(equipSound)),
                    SoundCategory.PLAYERS,
                    1.5f,
                    1.2f
            );
        }else{ //canceltask
            Bukkit.getLogger().warning("玩家取消任务");
            cancelGunReloadTask(event.getPlayer());
        }
    }
    @Override
    public void onRightClick(PlayerInteractEvent event) { //射击逻辑 右键点击
        long currentTime = System.currentTimeMillis();

        if(canShoot(currentTime)){
            setAmmo(getCurrentAmmo()-1);
            shoot(event.getPlayer());
            setLastShootTime(currentTime);
        }
    }
    protected boolean canShoot(long currentTime) {
        return getCurrentAmmo() > 0 &&
                !util.isOnCooldown(currentTime, lastShootTime, (long) rateOfFire * 1000);
    }
    protected void cancelGunReloadTask(Player player) {

        if (reloadTask != null && !reloadTask.isCancelled()) {
            reloadTask.cancel();
        }
        reloadTask=null;

        progressBar.clearUseProgress(player);
    }
    protected void shoot(Player player){
        player.getWorld().playSound(
                player.getLocation(),
                String.valueOf(NamespacedKey.fromString(fireSound)),
                SoundCategory.PLAYERS,
                1.5f,
                1.2f
        );
        performRaycast(player);
    }
    protected void performRaycast(Player player) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection();

        Vector finalDirection = calculateDynamicSpread(direction, player);

        World world = player.getWorld();
        double maxDistance;

        maxDistance=50;

        for (double distance = 0; distance <= maxDistance; distance += 1.0) {

            Location particleLoc = start.clone().add(finalDirection.clone().multiply(distance));
            if((distance / maxDistance) >= ScpOption.getInstance().getGunParticalStart()){
                world.spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
            }

            if (checkCollision(player, particleLoc)) {
                break;
            }
        }
    }
    protected boolean checkCollision(Player shooter, Location location) {
        TeamManager teamManager = TeamManager.getInstance();

        SCPPlayer shooterScpPlayer = teamManager.getAllPlayersMapping().get(shooter.getUniqueId());
        if(shooterScpPlayer==null)return false;
        for (Entity entity : location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)) {
            if (entity instanceof Player && entity != shooter && !entity.isDead()) {
                Player targetPlayer = (Player) entity;

                if(teamManager.getAllPlayersMapping().containsKey(targetPlayer.getUniqueId())){
                    SCPPlayer targetScpPlayer = teamManager.getAllPlayersMapping().get(targetPlayer.getUniqueId());

                    if(targetScpPlayer==null) continue;

                    if(isSameCamp(shooterScpPlayer, targetScpPlayer)){ //同一阵营取消攻击
                        continue;
                    }

                    Vector originalVelocity = targetPlayer.getVelocity();

                    SCPPlayer scpPlayer = teamManager.getAllPlayersMapping().get(targetPlayer.getUniqueId());
                    scpPlayer.getEntity().damaged(shooter, damage);

                    createTextDisPlay(location, damage);

                    // 立即恢复速度（移除击退）
                    Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(),
                            () -> {
                                if (targetPlayer.isValid() && !targetPlayer.isDead()) {
                                    targetPlayer.setVelocity(originalVelocity);
                                }
                            }, 1L);

                    location.getWorld().playSound(location,
                            Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    location.getWorld().spawnParticle(Particle.SMOKE,
                            location, 10, 0.3, 0.3, 0.3, 0.1);

                    return true;
                }
            }
        }

        if (!location.getBlock().isPassable()) {
            location.getWorld().playSound(location, Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 0.8f, 1.2f);
            location.getWorld().spawnParticle(Particle.FLAME, location, 8, 0.2, 0.2, 0.2, 0.1);
            return true;
        }

        return false;
    }
    protected boolean isSameCamp(SCPPlayer scpPlayer, SCPPlayer scpPlayer2){
        if(scpPlayer==null || scpPlayer2==null) return false;

        Map<RoleType, RoleTemplate> rolesTemplates = TeamManager.getInstance().getRolesTemplates();
        RoleType roleType = scpPlayer.getRoleType();
        RoleType roleType2 = scpPlayer2.getRoleType();

        RoleTemplate roleTemplate = rolesTemplates.get(roleType);
        RoleTemplate roleTemplate1 = rolesTemplates.get(roleType2);

        return roleTemplate == roleTemplate1;
    }
    protected void createTextDisPlay(Location location, double damage){
        // 创建 TextDisplay
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);
        textDisplay.setText(ChatColor.RED + "♥ " + damage);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setBackgroundColor(Color.fromARGB(0,0,0,0));
        textDisplay.setSeeThrough(true);
        textDisplay.setShadowed(true);

        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (textDisplay.isValid()) {
                textDisplay.remove();
            }
        }, 16L); // 0.8秒 = 16 ticks (20 ticks = 1秒)

    }
    protected Vector calculateDynamicSpread(Vector direction, Player player){
        Vector worldUp = new Vector(0,1,0);

        double x = direction.getY() * worldUp.getZ() - direction.getZ() * worldUp.getY();
        double y = direction.getZ() * worldUp.getX() - direction.getX() * worldUp.getZ();
        double z = direction.getX() * worldUp.getY() - direction.getY() * worldUp.getX();

        Vector right = new Vector(x,y,z).normalize();
        Vector up = right.crossProduct(direction).normalize();

        Random random = new Random();

        double baseSpread = getAccuracy();

        // 获取玩家速度（只考虑水平移动）
        Vector velocity = player.getVelocity();
        double horizontalSpeed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());

        // 速度影响系数
        double speedMultiplier = 1.0 + (horizontalSpeed * 3.0); // 每米/秒增加300%散布

        // 状态影响
        double stateMultiplier = 1.0;

        if (player.isSprinting()) {
            stateMultiplier *= 1.8; // 奔跑增加80%散布
        } else if (player.isSneaking()) {
            stateMultiplier *= 0.6; // 蹲下减少40%散布
        }

        if (!player.isOnGround()) {
            stateMultiplier *= 1.5; // 空中增加50%散布
        }

        double v = baseSpread * speedMultiplier * stateMultiplier;

        double horizontalOffset = (random.nextDouble() - 0.5) * v;
        double verticalOffset = (random.nextDouble() - 0.5) * v;

        Vector result = direction.clone()
                .add(right.multiply(horizontalOffset))
                .add(up.multiply(verticalOffset))
                .normalize();

        return result;
    }
}
