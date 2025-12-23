package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP106;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.scpoption.SCP106SpeicalSetting;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.*;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class SCP106Item extends AbstractSCPItem implements IOnPlayerHit, IOnLeftClick, IOnPlayerMove, IOnPlayerSneak {
    public SCP106SpeicalSetting scp106SpeicalSetting = SCP106SpeicalSetting.getInstance();

    //-----------------------------------------------------
    private final Map<SCPPlayer, Integer> attackCount = new HashMap<>();

    private long lastAttackTime = -1;

    private Player player;

    private boolean isLurk = false;

    private float energy = 0;
    private Location lastStayLocation;
    private float radius = 4.0f;

    private BukkitTask energyCheckTask;
    private BukkitTask displayMessageTask;
    private BukkitTask skillAnimatTask;

    private long lastUseSkillTime = -1;

    private boolean canThrough = true;

    public SCP106Item(String id, String disPlayName,
                      Material material, int customModelData,
                      List<String> lore, Player player) {
        super(id, disPlayName, material, customModelData, lore);

        this.player = player;
        this.lastStayLocation = player.getLocation();

        tick(player);
    }

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {
        //todo不清楚机制
//        event.getPlayer().sendMessage("你正在左键点击");
    }

    @Override
    public void OnPlayerHit(EntityDamageByEntityEvent event) {
        long currentTime = System.currentTimeMillis();
        if(util.isOnCooldown(currentTime, lastAttackTime,
                (long) (scp106SpeicalSetting.getAttackCD() * 1000)))return;
        lastAttackTime = currentTime;

        if(event.getEntity() instanceof Player target) {
            SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(target.getUniqueId());
            if(scpPlayer==null)return;

            int num = attackCount.get(scpPlayer);
            switch (num){
                case 0:
                    handleFirstAttack(scpPlayer);
                    break;
                case 1:
                    handleSecondAttack(scpPlayer);
                    break;
                case 2:
                    handleThreeAttack(scpPlayer);
                    break;
            }
        }
    }

    @Override
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if(skillAnimatTask != null && !skillAnimatTask.isCancelled()) return; //技能在使用中

        long currentTime = System.currentTimeMillis();
        if(util.isOnCooldown(currentTime, lastUseSkillTime,
                (long) (scp106SpeicalSetting.getSkillCD() * 1000))) return;
        lastUseSkillTime = currentTime;

        if(!isLurk){ //遁入地面
            if(energy <= 30)return;
            escapeToTheGround(player);
        } else {
            leaveTheGround(player);
        }
    }

    private void escapeToTheGround(Player player){
        isLurk = true;

        Location startLocation = player.getLocation();
        Location undergroundLocation = startLocation.clone().subtract(0, 2, 0);

        skillAnimatTask = new BukkitRunnable(){
            float t = 0;
            float duration = scp106SpeicalSetting.getSkillDuration();
            float d = 2.0f / (duration * 20);
            Location currentLoc = startLocation.clone();

            @Override
            public void run() {
                if(player == null || !player.isOnline() || player.isDead()){
                    this.cancel();
                    skillAnimatTask = null;
                    return;
                }



                if((t += 0.05) > duration){
                    this.cancel();
                    skillAnimatTask = null;

                    player.teleport(startLocation);
                    handleEscapeEnd();
                    player.setWalkSpeed(scp106SpeicalSetting.getMoveSpeedDuringSkill());
                    return;
                }


                currentLoc.subtract(0, d, 0);
                player.teleport(currentLoc);
            }

            private void handleEscapeEnd() {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.INVISIBILITY,
                        Integer.MAX_VALUE,
                        1,
                        false,
                        false
                ));

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(energy<=0){
                            this.cancel();
                            isLurk = false;
                            leaveTheGround(player);
                            return;
                        }

                        if(!isLurk){
                            this.cancel();
                            return;
                        }

                        handleEnergy(-0.1F);
                    }
                }.runTaskTimer(SCPMain.getInstance(),0L,1l);
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }
    private void leaveTheGround(Player player){
        isLurk = false;

        Location undergroundLocation = player.getLocation().subtract(0,2,0);
        Location groundLocation = player.getLocation();

        removeInvisibility();

        skillAnimatTask = new BukkitRunnable(){
            float t = 0;
            float duration = scp106SpeicalSetting.getSkillDuration();
            float d = 2.0f / (duration * 20);
            Location currentLoc = undergroundLocation.clone();

            @Override
            public void run() {
                if(player == null || !player.isOnline() || player.isDead()){
                    this.cancel();
                    skillAnimatTask = null;
                    return;
                }

                if((t += 0.05) > duration){
                    this.cancel();
                    skillAnimatTask = null;

                    player.teleport(groundLocation);
                    giveTemporarySpeedBoost();
                    return;
                }

                currentLoc.add(0, d, 0);
                player.teleport(currentLoc);
            }

            private void giveTemporarySpeedBoost() {
                player.setWalkSpeed((float) (0.2 * scp106SpeicalSetting.getMoveSpeedDuringSkill()));

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(player != null && player.isOnline()){
                            player.setWalkSpeed(0.2f);
                        }
                    }
                }.runTaskLater(SCPMain.getInstance(), 60L);  // 60 ticks = 3秒
            }

        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }

    private void removeInvisibility() {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.setWalkSpeed((float) (0.2f * (0.2 * 0.2)));
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) { //穿墙
        handleGlassPaneTeleport(event.getPlayer());
    }

    public void handleGlassPaneTeleport(Player player) {
        Location playerLoc = player.getLocation();
        Block nearestPane = null;
        double nearestDist = Double.MAX_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = playerLoc.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.PINK_STAINED_GLASS_PANE) {
                        Location center = block.getLocation().add(0.5, 0.5, 0.5);
                        double dist = playerLoc.distance(center);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearestPane = block;
                        }
                    }
                }
            }
        }

        if (nearestPane != null && nearestDist <= 0.75 && canThrough) {
            canThrough = false;
            Location paneCenter = nearestPane.getLocation().add(0.5, 0.5, 0.5);

            Vector toPane = paneCenter.toVector().subtract(playerLoc.toVector());

            Vector direction = toPane.clone().normalize();

            direction.setY(0);

            if (direction.lengthSquared() > 0) {
                direction.normalize();

                Vector teleportVector = direction.multiply(0.5); // 0.5格

                float yaw = playerLoc.getYaw();
                float pitch = playerLoc.getPitch();

                Location newLocation = playerLoc.clone().add(teleportVector);
                newLocation.setYaw(yaw);
                newLocation.setPitch(pitch);

                player.teleport(newLocation);
            }
        }else if((nearestDist > 0.75 || nearestPane == null) && canThrough == false){
            canThrough = true;
        }
    }

    private void handleFirstAttack(SCPPlayer scpPlayer) {
        Player target = scpPlayer.getPlayer();
        GameEntity targetEntity = scpPlayer.getEntity();
        targetEntity.damaged(player, 40);
        attackCount.put(scpPlayer, 1);
        new BukkitRunnable(){
            @Override
            public void run() { //持续20秒，伤害总数为42的任务
                if(!target.isOnline() || target.isDead()){
                    this.cancel();
                    return;
                }

                targetEntity.damaged(player,2.1f);
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 20L);
    }
    private void handleSecondAttack(SCPPlayer scpPlayer) {
        Player target = scpPlayer.getPlayer();
        target.teleport(scp106SpeicalSetting.getPocketSpaceLocation());
        attackCount.put(scpPlayer, 2);
    }
    private void handleThreeAttack(SCPPlayer scpPlayer) {
        scpPlayer.getEntity().damaged(player,-1);
        attackCount.put(scpPlayer, 3);
    }

    private void handleEnergy(float value){
        float temp = energy + value;
        this.energy = Math.max(0, Math.min(temp, 100));  //保证其大于0小于100
    }

    public void tick(Player player){
        createEnergyCheckTask(player);  //检测精力恢复
        createDisplayTask(player);  //显示信息
    }

    private void createEnergyCheckTask(Player player) {
        energyCheckTask = new BukkitRunnable(){

            @Override
            public void run() {
                if(!player.isOnline() || player.isDead())return;
                if(isLurk)return;
                if(player.getLocation().distance(lastStayLocation) < radius)return;
                lastStayLocation = player.getLocation();

                new BukkitRunnable(){ //创建一个总共恢复10点精力的任务， 持续一秒钟
                    float t = 0;
                    float addEnergy = 0.3f;
                    @Override
                    public void run() {
                        if((t+=0.1)>=1){
                            this.cancel();
                            return;
                        }

                        handleEnergy(addEnergy);
                    }
                }.runTaskTimer(SCPMain.getInstance(),0L, 2L);
            }
        }.runTaskTimer(SCPMain.getInstance(),0L,20L);
    }

    private void createDisplayTask(Player player) {
        displayMessageTask = new BukkitRunnable(){
            @Override
            public void run() {
                if(!player.isOnline() || player.isDead() ||
                        !(TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId()).getEntity() instanceof SCP_106)){
                    this.cancel();
                    return;
                }

                long currentTime = System.currentTimeMillis();

                String attackStatus = getSkillStatus(currentTime, lastAttackTime,
                        (long)(scp106SpeicalSetting.getAttackCD() * 1000), "M1");

                String shiftSkillStatus = getSkillStatus(currentTime, lastUseSkillTime,
                        (long)(scp106SpeicalSetting.getSkillCD() * 1000), "Shift");

                String energyCnt = "能量: " + (int) energy;

                String message = String.format("§6技能: %s §f| %s §f| %s",
                        attackStatus, shiftSkillStatus, energyCnt);

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(message));
            }

            private String getSkillStatus(long currentTime, long lastUseTime, long cooldown, String skillName) {
                if (lastUseTime == -1) {
                    return "§a" + skillName + ":就绪";
                }

                long elapsed = currentTime - lastUseTime;
                if (elapsed >= cooldown) {
                    return "§a" + skillName + ":就绪";
                } else {
                    long remaining = (cooldown - elapsed) / 1000 + 1;
                    return "§c" + skillName + ":" + remaining + "s";
                }
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }
}
