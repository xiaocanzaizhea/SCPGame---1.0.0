package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.scpoption.SCP049SpiecalSetting;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049.scp0492.SCP_0492;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.*;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.DeathData;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.ScpType;
import xyz.xiaocan.tools.util;

import java.util.*;

public class MedicalDevice extends AbstractSCPItem
        implements IOnPlayerHit, IOnSwapHandClick, IOnPlayerSneak, IOnSwitchItemBar, IOnRightClick, IOnLeftClick {

    private SCP049SpiecalSetting scp049SpiecalSetting;

    //--------------------显示049所有技能信息的一个任务-------------------
    private BukkitTask displayMessageTask;

    private Player player;

    private long rescueStartTime = - 1;
    private boolean isRescue = false;
    private float rescueProgress = 0;

    private final Map<SCPPlayer, Integer> attackCount = new HashMap<>(); //记录对独一无二的scpplayer攻击的次数
    private final Map<SCPPlayer, BukkitTask> markedPlayers = new HashMap<>();
    private double sourceSpeed;

    //--------------------记录上次使用时间-----------------------------------
    private long lastLeftHitTime = -1;
    private long lastFSkillTime = -1;
    private long lastShiftTime = -1;

    public MedicalDevice(String id, String disPlayName,
                         Material material, int customModelData,
                         List<String> lore, Player player) {
        super(id, disPlayName, material, customModelData, lore);

        this.player = player;

        scp049SpiecalSetting = SCP049SpiecalSetting.getInstance();
        sourceSpeed = 0.2f;

        tick(player); //显示和逻辑任务
    }
    @Override
    public void onSwapHandClick(PlayerSwapHandItemsEvent event) {
        long currentTime = System.currentTimeMillis();
        if(util.isOnCooldown(currentTime,
                lastFSkillTime, (long)(scp049SpiecalSetting.getFSkillCooldown() * 1000)))return;
        lastFSkillTime = currentTime;

        Player player = event.getPlayer();
        if(!isSCP049(player))return;

        Player findPlayer = util.findGazePlayer(player);
        if(findPlayer==null){
            lastFSkillTime = (long) (currentTime - 2.5 * 1000);
            return;
        }

        SCPPlayer target = TeamManager.getInstance().getSCPPlayer(player);
        if(target==null){
            lastFSkillTime = (long) (currentTime - 2.5 * 1000);
            return;
        }

        //第一次标记
        if(!markedPlayers.containsKey(target)){

            float addSpeed =(float) scp049SpiecalSetting.getFSkillSpeedAdd();
            player.setWalkSpeed((float)(sourceSpeed + addSpeed));
            markedPlayers.put(target,
                    new BukkitRunnable() {
                        float t = 0;
                        @Override
                        public void run() {
                            t++;
                            if(t>= scp049SpiecalSetting.getFSkillCooldown()){
                                player.setWalkSpeed((float)sourceSpeed);
                                this.cancel();
                            }

                            if(target.getPlayer().isOnline()
                                    && !target.getPlayer().isDead()){
                                //在目标头部生成
                                util.createTextDidPlay(
                                        target.getPlayer().getLocation().clone().
                                                add(0, 2.3, 0));
                            }
                        }
                    }.runTaskTimer(SCPMain.getInstance(),0l,20l));
        }else{
            event.getPlayer().sendMessage("你无法二次标记同一玩家");
        }
    }
    @Override
    public void OnPlayerHit(EntityDamageByEntityEvent event) {

        long currentTime = System.currentTimeMillis();
        if(util.isOnCooldown(currentTime, lastLeftHitTime,
                (long)(scp049SpiecalSetting.getAttackCooldown() * 1000)))return;

        lastLeftHitTime = currentTime;

        if (event.getDamager() instanceof Player source && event.getEntity() instanceof Player target) {
            SCPPlayer scpPlayer = TeamManager.getInstance().getSCPPlayer(target);
            if(scpPlayer==null)return;
            if(scpPlayer.getEntity() instanceof SCPEntity)return;

            if(!attackCount.containsKey(scpPlayer)){
                attackCount.put(scpPlayer, 1);
                new BukkitRunnable(){
                    long t=0;
                    double damage = scp049SpiecalSetting.getDamage()
                            / scp049SpiecalSetting.getDamageDuringTime();
                    @Override
                    public void run() {
                        if(t++>scp049SpiecalSetting.getDamageDuringTime()
                                || scpPlayer.getPlayer().getGameMode()==GameMode.SPECTATOR
                                || !scpPlayer.getPlayer().isOnline()){
                            this.cancel();
                            return;
                        }

                        scpPlayer.getEntity().damaged(source, damage);
                    }
                }.runTaskTimer(SCPMain.getInstance(),0l,20l);  //1s运行一次
            }else{
                //秒杀
                scpPlayer.getEntity().damaged(source,
                        scpPlayer.getEntity().getMaxHp()
                                + scpPlayer.getEntity().getCurrentShield());
            }
        }
    }

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {
        lastLeftHitTime = System.currentTimeMillis();
    }

    @Override //复活技能
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if(event.isSneaking()){
            Bukkit.getLogger().warning("玩家开始蹲下");
            isRescue = true;
            rescueStartTime = System.currentTimeMillis();

            long currentTime = System.currentTimeMillis();
            Map<UUID, DeathData> allDeathData = TeamManager.getInstance().getAllDeathData();

            for (DeathData d : allDeathData.values()) {
                if(player.getLocation().distance(d.location)<=3
                        && util.isOnCooldown(currentTime,d.deathTime,
                        (long) (scp049SpiecalSetting.getInvalidTimeRange() * 1000))
                                && d.deathPlayer.getGameMode()==GameMode.SPECTATOR ){ //执行救助逻辑

                            new BukkitRunnable(){
                                float t = 0;
                                @Override
                                public void run() {
                                    if(rescueProgress>=1){
                                        this.cancel();
                                        spawnSCP0492(d);
                                        return;
                                    }

                                    rescueProgress = (float) ((t+=0.05) / scp049SpiecalSetting.getHelpTime());
                                }

                                private void spawnSCP0492(DeathData d) {
                                    Player deathPlayer = d.getDeathPlayer();
                                    util.initPlayerDataAdven(deathPlayer);
                                    RoleTemplate roleTemplate = TeamManager.getInstance().getRolesTemplates().get(ScpType.SCP0492);
                                    SCPPlayer scpPlayer = new SCPPlayer(d.getDeathPlayer(),
                                            new SCP_0492(d.deathPlayer, roleTemplate), ScpType.SCP0492);
                                    deathPlayer.teleport(d.location);

                                    //handleData
                                    TeamManager.getInstance().getAllPlayersMapping().put(deathPlayer.getUniqueId(), scpPlayer);
                                    d.cleanup();
                                }
                            }.runTaskTimer(SCPMain.getInstance(),0l,1l);

                            break;
                }
            }
        }else{
            Bukkit.getLogger().warning("玩家蹲起结束");
            rescueProgress = 0;
            isRescue = false;
        }
    }
    @Override //R skill
    public void onRightClick(PlayerInteractEvent event) {
        long currentTime = System.currentTimeMillis();
        if(util.isOnCooldown(currentTime, lastShiftTime,
                (long)(scp049SpiecalSetting.getRSkillColldown() * 1000)))return;
        lastShiftTime = currentTime;

        double radius = scp049SpiecalSetting.getRSkillRadius();
        double duration = scp049SpiecalSetting.getRSkillDuringTime();
        double shieldPerSecond = scp049SpiecalSetting.getTotalShield()
                / duration;
        Player player = event.getPlayer();

        new BukkitRunnable() {
            float ticks = 0;

            @Override
            public void run() {
                if ((ticks+=0.05) >= duration || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (ticks % 10 == 0) {
                    healPlayerInRadius(player, radius,shieldPerSecond * 0.5);
                }
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1L);
    }
    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){

        }else{
            cancelTask(event.getPlayer(),displayMessageTask);
        }
    }

    public boolean isSCP049(Player player){
        SCPPlayer scpPlayer = TeamManager.getInstance()
                .getAllPlayersMapping().get(player.getUniqueId());

        return scpPlayer!=null
                && (scpPlayer.getEntity() instanceof SCP_049);
    }

    private void healPlayerInRadius(Player player, double radius, double shieldAmount){
        Location center = player.getLocation();

        for (Player otherPlayer: Bukkit.getOnlinePlayers()) {
            if(otherPlayer.getLocation().distance(center) <= radius){
                if(otherPlayer.isOnline()
                        && !otherPlayer.isDead()){

                    SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(otherPlayer.getUniqueId());
                    if(scpPlayer!=null){
                        scpPlayer.getEntity().setCurrentShield(scpPlayer.getEntity().getCurrentShield() + shieldAmount);
                    }
                }
            }
        }
    }

    private void tick(Player player){
        createDisPlayTask(player);
    }

    private void createDisPlayTask(Player player) {
        displayMessageTask = new BukkitRunnable(){
            @Override
            public void run() {
                if(!player.isOnline() || player.isDead() ||
                        !(TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId()).getEntity() instanceof SCP_049)){
                    this.cancel();
                    return;
                }

                long currentTime = System.currentTimeMillis();

                String fSkillStatus = getSkillStatus(currentTime, lastFSkillTime,
                        (long)(scp049SpiecalSetting.getFSkillCooldown() * 1000), "F");

                String attackStatus = getSkillStatus(currentTime, lastLeftHitTime,
                        (long)(scp049SpiecalSetting.getAttackCooldown() * 1000), "M1");

                String rSkillStatus = getSkillStatus(currentTime, lastShiftTime,
                        (long)(scp049SpiecalSetting.getRSkillColldown() * 1000), "Shift");

                String message;
                if (isRescue && rescueStartTime > 0) {
                    int percentage = (int) (rescueProgress * 100);

                    String progressColor;
                    if (percentage < 30) {
                        progressColor = "§c"; // 红色 0-29%
                    } else if (percentage < 60) {
                        progressColor = "§6"; // 金色 30-59%
                    } else if (percentage < 90) {
                        progressColor = "§e"; // 黄色 60-89%
                    } else {
                        progressColor = "§a"; // 绿色 90-100%
                    }

                    String rescueProgress = String.format("§6救人: %s%d%%", progressColor, percentage);

                    message = String.format("§6技能: %s §f| %s §f| %s §f| %s",
                            fSkillStatus, attackStatus, rSkillStatus, rescueProgress);
                } else {
                    // 非救人状态：只显示技能冷却
                    message = String.format("§6技能: %s §f| %s §f| %s",
                            fSkillStatus, attackStatus, rSkillStatus);
                }

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
