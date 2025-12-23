package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP173;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.scpoption.SCP173SpiecalSetting;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.*;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.util;

import java.util.List;

public class Mushroom extends AbstractSCPItem
        implements IOnPlayerHit, IOnRightClick, IOnPlayerSneak
        , IOnSwapHandClick, IOnPlayerMove, IOnSwitchItemBar {

    private SCP173SpiecalSetting scp173SpiecalSetting;

    private BukkitTask displayMessageTask; //任务显示

    private Player player; //绑定的173 player

    private ArmorStand currentArmorStand = null;
    private BukkitTask armorTask;
    private boolean armorState = true; //这个state控制第一次点击（生成盔甲架） 第二次点击（传送）的转化

    //------上次使用时间-----
    private long lastTeleportTime = -1;
    private long lastHighSpeedTime = -1;
    private long lastMudTime = -1;


    private boolean isGazedBySomeBody = false;   //用于确定是否可以移动
    private boolean canTeleport = false;   //用于确定是否可以传送
    private boolean isHighSpeed = false;

    private double gazedTime;

    //-------tp----------
    private float originCD_OF_TELEPORT;
    private float currentCD_OF_TELEPORT;

    public Mushroom(String id, String disPlayName,
                    Material material, int customModelData, List<String> lore, Player player) {
        super(id, disPlayName, material, customModelData, lore);

        this.player = player;
        this.scp173SpiecalSetting = SCP173SpiecalSetting.getInstance();

        this.originCD_OF_TELEPORT = scp173SpiecalSetting.getCdOfTeleport();
        this.currentCD_OF_TELEPORT = originCD_OF_TELEPORT;

        createPlayerGazeTask();
    }

    //<editor-fold desc="凝视相关">
    private void createPlayerGazeTask() {
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!player.isOnline() || player.isDead()){
                    this.cancel();
                }

                tryGetPlayerGaze();
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,1l);
    }

    private void tryGetPlayerGaze(){
        for (Player otherPlayer: Bukkit.getOnlinePlayers()) {
            GameMode gameMode = otherPlayer.getGameMode();
            if(gameMode!=GameMode.ADVENTURE)continue;

            Player target = util.findGazeScp173(otherPlayer);

            //只要一个玩家注释scp173,就结束
            if(target==player){
                isGazedBySomeBody = true;    //确定为不可移动

                if(canTeleport==false){
                    gazedTime+=0.05;  //tick转化为秒
                }

                if(gazedTime>=currentCD_OF_TELEPORT){
                    gazedTime = 0;
                    canTeleport = true;
                }
                return;
            }
        }
        isGazedBySomeBody = false;
    }
    //</editor-fold>
    @Override
    public void OnPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player source && event.getEntity() instanceof Player target) {

            SCPPlayer sourceSCPPlyaer = TeamManager.getInstance().getAllPlayersMapping().get(source.getUniqueId());
            SCPPlayer targetSCPPlyaer = TeamManager.getInstance().getAllPlayersMapping().get(target.getUniqueId());

            if(canAttack(sourceSCPPlyaer, targetSCPPlyaer)){

                Vector sourceDirection = source.getEyeLocation().getDirection();
                Vector targetDirection = target.getEyeLocation().getDirection();
                double dotProduct = sourceDirection.dot(targetDirection);
                double cos45 = Math.cos(Math.toRadians(45));

                if(dotProduct <= cos45)return;

                targetSCPPlyaer.getEntity().damaged(source.getPlayer(),scp173SpiecalSetting.getDamage());
            }
        }
    }
    @Override
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(isSCP173(player)){
            long currentTime = System.currentTimeMillis();
            if (util.isOnCooldown(currentTime, lastMudTime,
                    (long) (scp173SpiecalSetting.getCdOfMud() * 1000)))return;
            lastMudTime = currentTime;

            createMudPuddle(player);
            player.playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 0.8f);
        }
    }
    @Override
    public void onRightClick(PlayerInteractEvent event) {
        if(!isSCP173(event.getPlayer()))return;

        Player player = event.getPlayer();
        SCP_173 scp173 = (SCP_173) TeamManager.getInstance()
                .getSCPPlayer(player).getEntity();

        if(canTeleport==false)return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if(armorState){     //这里为准备好后第一次按右键,开始生成盔甲架
                    if(armorTask==null || armorTask.isCancelled()){
                        if(currentArmorStand != null && !currentArmorStand.isDead()){
                            currentArmorStand.remove();
                            currentArmorStand = null;
                        }

                        armorTask = new BukkitRunnable(){
                            int t = 0;

                            @Override
                            public void run() {
                                //生成一个随视线移动并且会朝向玩家的盔甲架
                                t += 0.05;
                                Location location = getGazeCollisionPoint(scp173.getPlayer());
                                location.setDirection(scp173.getPlayer().getLocation().subtract(location).toVector());

                                if(location==null)return;

                                if(currentArmorStand == null || currentArmorStand.isDead()){
                                    // 第一次创建盔甲架
                                    currentArmorStand = createArmorStandMarked(scp173.getPlayer());
                                } else {
                                    // 更新现有盔甲架位置
                                    currentArmorStand.teleport(location);
                                }

                                if(t>=3){  //超过时间后
                                    canTeleport = false;
                                    cleanup();
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(SCPMain.getInstance(),0l,1l);
                    }

                    armorState = false;  //切换状态
                }else{
                    // 第二次右键 - 执行瞬移操作
                    if(armorTask != null){
                        armorTask.cancel();
                        armorTask = null;
                    } //取消任务

                    /**
                     * 非高速状态下才可以杀人
                     */
                    if(!isHighSpeed){
                        Player nearest = util.findNearestPeople(
                                player, scp173SpiecalSetting.getRadius(),
                                currentArmorStand.getLocation());
                        if(nearest!=null){
                            SCPPlayer nearestScpPlayer = TeamManager.
                                    getInstance().getAllPlayersMapping().get(nearest.getUniqueId());

                            if(nearestScpPlayer!=null){
                                nearestScpPlayer.getEntity().
                                        damaged(player, scp173SpiecalSetting.getDamage());
                            }
                        }
                    }

                    // 执行瞬移到盔甲架位置
                    if(currentArmorStand != null && !currentArmorStand.isDead()){
                        Location targetLocation = currentArmorStand.getLocation();
                        targetLocation.setDirection(player.getEyeLocation().getDirection());
                        targetLocation.setPitch(player.getLocation().getPitch());
                        targetLocation.setYaw(player.getLocation().getYaw());
                        player.teleport(targetLocation);

                        currentArmorStand.remove();
                        currentArmorStand=null;
                    }

                    armorState = true; // 切换状态
                    canTeleport = false;
                }
        }
    }

    @Override
    public void onSwapHandClick(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(isSCP173(player)){
            SCPPlayer scp173 = TeamManager.getInstance().getSCPPlayer(player);
            float origin = (float) scp173.getEntity().getOriginSpeed();

            if(isHighSpeed){ //提前结束超高速
                isHighSpeed = false;
                scp173.getPlayer().setWalkSpeed(origin);

                currentCD_OF_TELEPORT = originCD_OF_TELEPORT;//恢复cd
                updateCurrentArmorStand(scp173.getPlayer());
            }else{
                long currentTime = System.currentTimeMillis();
                if(util.isOnCooldown(currentTime, lastHighSpeedTime,
                        (long) (scp173SpiecalSetting.getCdOfHighSpeed() * 1000)))return;
                lastHighSpeedTime = currentTime;

                currentCD_OF_TELEPORT = (float) (originCD_OF_TELEPORT
                        * scp173SpiecalSetting.getPercentOfTeleportTime());

                scp173.getPlayer().setWalkSpeed((float) (origin
                        + scp173SpiecalSetting.getHighSpeedAdd()));
                isHighSpeed = true;
                updateCurrentArmorStand(scp173.getPlayer());

                new BukkitRunnable(){//超高速技能时间结束
                    float t=0;
                    @Override
                    public void run() {
                        if((t+=0.5) >= scp173SpiecalSetting.getHighSpeedDuringTime()){
                            isHighSpeed = false;
                            scp173.getPlayer().setWalkSpeed(origin);
                            updateCurrentArmorStand(player);
                            currentCD_OF_TELEPORT = originCD_OF_TELEPORT;
                            this.cancel();
                        }
                    }
                }.runTaskTimer(SCPMain.getInstance(), 0l, 10l);
            }
        }
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){
            createTask(event.getPlayer());
        }else{
            cancelTask(event.getPlayer(), displayMessageTask);
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isSCP173(event.getPlayer()))return;

        Location from = event.getFrom();
        Location to = event.getTo();
        boolean positionChange = from.getX() != to.getX() || from.getZ() != to.getZ();

        if(!isGazedBySomeBody)return;

        if (positionChange) {
            event.setTo(from);
        }
    }

    private boolean canAttack(SCPPlayer sourceSCPPlyaer, SCPPlayer targetSCPPlyaer){
        if(sourceSCPPlyaer!=null && targetSCPPlyaer!=null){
            if(sourceSCPPlyaer.getEntity() instanceof SCP_173 && !(targetSCPPlyaer.getEntity() instanceof SCPEntity)){
                return true;
            }
        }
        return false;
    }

    private boolean isSCP173(Player player){
        SCPPlayer scpPlayer = TeamManager.getInstance()
                .getAllPlayersMapping().get(player.getUniqueId());

        return scpPlayer!=null
                && (scpPlayer.getEntity() instanceof SCP_173);
    }

    private Location getGazeCollisionPoint(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        float maxDistance = (float) scp173SpiecalSetting.getTeleportDistance();
        if(isHighSpeed){
            maxDistance += scp173SpiecalSetting.getHighSpeedDistanceAdd();
        }

        RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(
                eyeLocation,
                direction,
                maxDistance,
                FluidCollisionMode.NEVER,
                true
        );

        if (rayTrace != null && rayTrace.getHitBlock() != null) {
            Location hitLocation = rayTrace.getHitPosition().toLocation(player.getWorld());
            BlockFace hitFace = rayTrace.getHitBlockFace();

            return adjustLocationOutsideBlock(hitLocation, hitFace);
        } else {
            Location location = eyeLocation.add(direction.multiply(maxDistance - 1));
            location = changeY(location,0);
            return location;  //未击中方块
        }
    }

    private Location changeY(Location location, int depth){

        // 检查当前方块坐标位置
        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        // 如果找到非空气方块或达到最大深度
        if((!block.getType().isAir()) || depth >= 8){
            return new Location(location.getWorld(), location.getX(), location.getBlockY() + 1, location.getZ());
        }

        // 向下移动一个方块
        Location newLocation = new Location(location.getWorld(), location.getX(),
                location.getBlockY() - 1, location.getZ());
        return changeY(newLocation, depth + 1);
    }

    private Location adjustLocationOutsideBlock(Location hitLocation, BlockFace hitFace) {
        Location adjustedLocation = hitLocation.clone();

        switch (hitFace) {
            case UP:
                adjustedLocation.add(0, 0.1, 0);
                break;
            case DOWN:
                adjustedLocation.subtract(0, 2, 0);
                break;
            case NORTH:
                adjustedLocation.add(0, 0, -0.5);
                break;
            case SOUTH:
                adjustedLocation.add(0, 0, 0.5);
                break;
            case EAST:
                adjustedLocation.add(0.5, 0, 0);
                break;
            case WEST:
                adjustedLocation.add(-0.5, 0, 0);
                break;
            default:
        }

        return adjustedLocation;
    }

    private void cleanup() {// 清理盔甲架方法
        if(currentArmorStand != null && !currentArmorStand.isDead()){
            currentArmorStand.remove();
            currentArmorStand = null;
        }
        if(armorTask != null && !armorTask.isCancelled()){
            armorTask.cancel();
            armorTask = null;
        }
        armorState = true;
    }

    //<editor-fold desc="生成传送可视化">
    private void updateCurrentArmorStand(Player player){
        if(currentArmorStand!=null && !currentArmorStand.isDead()){
            Location location = currentArmorStand.getLocation();
            currentArmorStand.remove();
            Bukkit.getLogger().info("成功移除盔甲架");
            // 创建新的盔甲架
            currentArmorStand = createArmorStandMarked(player);
        }
    }
    private ArmorStand createArmorStandMarked(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();

        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location,
                EntityType.ARMOR_STAND);

        armorStand.setVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setCustomNameVisible(false);

        armorStand.setBasePlate(false);
        armorStand.setArms(true);
        armorStand.setSmall(false);
        armorStand.setMarker(false);

        armorStand.setCanPickupItems(false);

        armorStand.setMarker(true);

        armorStand.setCollidable(false);
        armorStand.setAI(false);
        armorStand.setPersistent(false);

        armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING);

        armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.REMOVING_OR_CHANGING);

        // 给盔甲架穿上全套皮革装备,超高速为白色，普通为红色
        equipLeatherArmor(armorStand);

        for (Player onlinePlayer:Bukkit.getOnlinePlayers()) {
            onlinePlayer.hideEntity(SCPMain.getInstance(), armorStand);
        }

        player.showEntity(SCPMain.getInstance(),armorStand);
        Bukkit.getLogger().info("生成了一个盔甲架");
        return armorStand;
    }
    private void equipLeatherArmor(ArmorStand armorStand) {
        Color redColor = Color.RED;
        Color whiteColor = Color.WHITE;

        // 创建红色皮革头盔
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        if(isHighSpeed==false){
            helmetMeta.setColor(redColor);
        }else{
            helmetMeta.setColor(whiteColor);
        }
        helmet.setItemMeta(helmetMeta);

        // 创建红色皮革胸甲
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        if(isHighSpeed==false){
            chestplateMeta.setColor(redColor);
        }else{
            chestplateMeta.setColor(whiteColor);
        }
        chestplate.setItemMeta(chestplateMeta);

        // 创建红色皮革护腿
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        if(isHighSpeed==false){
            leggingsMeta.setColor(redColor);
        }else{
            leggingsMeta.setColor(whiteColor);
        }
        leggings.setItemMeta(leggingsMeta);

        // 创建红色皮革靴子
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        if(isHighSpeed==false){
            bootsMeta.setColor(redColor);
        }else{
            bootsMeta.setColor(whiteColor);
        }
        boots.setItemMeta(bootsMeta);

        // 给盔甲架装备上
        armorStand.getEquipment().setHelmet(helmet);
        armorStand.getEquipment().setChestplate(chestplate);
        armorStand.getEquipment().setLeggings(leggings);
        armorStand.getEquipment().setBoots(boots);
    }
    //</editor-fold>

    //<editor-fold desc="发怒技能">
    private void createMudPuddle(Player player){
        Location mudLocation = player.getLocation().clone().add(0, 0.1, 0); // 在脚下稍微上方

        util.playSoundEffects(player.getLocation(),Sound.BLOCK_SLIME_BLOCK_PLACE, 1.0f, 0.8f);

        new BukkitRunnable() {
            float t = 0;
            @Override
            public void run() {
                if (t >= scp173SpiecalSetting.getMudDuringTime()) {
                    this.cancel();
                    return;
                }

                spawnMudParticles(mudLocation);

                affectPlayersInRange(mudLocation);

                t+=0.25;
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 5L);
    }

    private void spawnMudParticles(Location location) {
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = Math.cos(angle) * 2.5; // 2.5格半径
            double z = Math.sin(angle) * 2.5;

            Location particleLoc = location.clone().add(x, 0, z);

            // 使用BLOCK_CRACK粒子显示棕色混凝土粉末
            location.getWorld().spawnParticle(
                    Particle.BLOCK_MARKER,
                    particleLoc,
                    3, // 数量
                    0.2, 0.1, 0.2, // 偏移
                    0.05, // 速度
                    Material.BROWN_CONCRETE_POWDER.createBlockData()
            );

            // 添加一些水滴粒子增强效果
            location.getWorld().spawnParticle(
                    Particle.DRIPPING_WATER,
                    particleLoc,
                    1,
                    0.1, 0.1, 0.1,
                    0.1
            );
        }
        location.getWorld().spawnParticle(
                Particle.BLOCK_MARKER,
                location,
                5,
                0.5, 0.1, 0.5,
                0.1,
                Material.BROWN_CONCRETE_POWDER.createBlockData()
        );
    }

    private void affectPlayersInRange(Location mudLocation) {
        double radius = 3.0;

        for (Player nearbyPlayer : mudLocation.getWorld().getPlayers()) {
            if (isSCP173(nearbyPlayer))continue;

            if (nearbyPlayer.getLocation().distance(mudLocation) <= radius) {
                nearbyPlayer.addPotionEffect(new PotionEffect(
                        PotionEffectType.SLOWNESS,
                        80,
                        3,
                        true,
                        true,
                        true
                ));

                nearbyPlayer.spawnParticle(
                        Particle.ANGRY_VILLAGER,
                        nearbyPlayer.getLocation().add(0, 1, 0),
                        1
                );
            }
        }
    }


    //</editor-fold>

    private void createTask(Player player) {
        displayMessageTask = new BukkitRunnable(){
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() ||
                        !(TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId()).getEntity() instanceof SCP_173)) {
                    this.cancel();
                    return;
                }

                long currentTime = System.currentTimeMillis();

                // 凝视时间显示
                String gazeStatus = getGazeStatus();

                // 传送状态显示
                String teleportStatus = getTeleportStatus();

                // F技能超高速冷却时间
                String fSkillStatus = getSkillStatus(currentTime, lastHighSpeedTime,
                        (long)(scp173SpiecalSetting.getCdOfHighSpeed() * 1000), "F");

                // Shift烂泥冷却时间
                String shiftSkillStatus = getSkillStatus(currentTime, lastMudTime,
                        (long)(scp173SpiecalSetting.getCdOfMud() * 1000), "Shift");

                // 超高速状态显示
                String highSpeedStatus = getHighSpeedStatus();

                String message = String.format("§6凝视: %s §f| 传送: %s §f| %s §f| %s §f| %s",
                        gazeStatus, teleportStatus, fSkillStatus, shiftSkillStatus, highSpeedStatus);

                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(message));
            }

            private String getGazeStatus() {
                if (!isGazedBySomeBody) {
                    return "§a无人凝视";
                } else {
                    return "§c被凝视";
                }
            }

            private String getTeleportStatus() {
                if (canTeleport) {
                    return "§a可传送";
                } else {
                    // 显示剩余时间
                    double remaining = currentCD_OF_TELEPORT - gazedTime;
                    if (remaining < 0) remaining = 0;
                    return String.format("§e%.1fs", remaining);
                }
            }

            private String getSkillStatus(long currentTime, long lastUseTime, long cooldown, String skillName) {
                // 如果是F技能且正在超高速状态中，显示持续时间
                if (skillName.equals("F") && isHighSpeed) {
                    return "§b超高速中"; // 超高速进行中
                }

                if (lastUseTime == -1) {
                    return "§a" + skillName; // 从未使用过，显示绿色技能名
                }

                long elapsed = currentTime - lastUseTime;
                if (elapsed >= cooldown) {
                    return "§a" + skillName; // 冷却完毕，只显示绿色技能名
                } else {
                    long remainingSeconds = (cooldown - elapsed + 999) / 1000; // 向上取整
                    return "§c" + skillName + ":" + remainingSeconds + "s"; // 冷却中，显示剩余时间
                }
            }

            private String getHighSpeedStatus() {
                if (isHighSpeed) {
                    return "§b超高速";
                } else {
                    return "§7普通";
                }
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 10L); // 每0.5秒更新一次
    }
}
