package xyz.xiaocan.scpEntity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.chatsystem.ChatChannel;
import xyz.xiaocan.chatsystem.DistanceChatManager;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpmanager.TabManager;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.scpsystems.respawnsystem.RespawnSystem;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.tools.util;

import java.util.Map;
import java.util.UUID;


@Getter
@Setter
public abstract class GameEntity {

    protected String displayName;

    protected double currentShield;
    protected double maxShield;

    protected double currentHp;
    protected double maxHp;

    protected double armor;

    protected double originSpeed;
    protected double moveSpeed;

    protected Player player;
    protected RoleTemplate roleTemplate;

    // 添加经验条更新相关字段
    protected boolean showHealthBar = true;
    protected long lastHealthUpdate = 0;

    // 血量显示任务
    protected BukkitRunnable expUpdateTask;

    // 构造方法,核心属性都靠构造方法来赋值
    public GameEntity(Player player, RoleTemplate roleTemplate) {
        this.player = player;
        this.roleTemplate = roleTemplate;
        this.maxHp = roleTemplate.getMaxHp();
        this.currentHp = maxHp;
        this.armor = roleTemplate.getArmor();
        this.moveSpeed = roleTemplate.getMoveSpeed();
        this.displayName = roleTemplate.getDisPlayName();
        this.maxShield = roleTemplate.getMaxShield();

        this.originSpeed = moveSpeed;
        player.setWalkSpeed((float)moveSpeed);

        //初始玩家状态
        util.initPlayerDataAdven(player);

        spawn();
        update();

        DistanceChatManager
                .playerModes
                .put(player.getUniqueId(), ChatChannel.LOCAL);
    }

    protected void sendTitle(){}

    //<editor-fold desc="玩家生成的逻辑">
    //处理玩家生成后要处理的逻辑
    protected void spawn(){
        Location location = TeamManager.getInstance(). //传送
                getTeamSpawnPoints().get(roleTemplate.getRoleType());

        if(location!=null){
            player.teleport(location);
        }else{
            Bukkit.getLogger().warning("GameEntity传送出错" +
                    player.getName() + "角色为" + roleTemplate.getDisPlayName());
        }
    };

    protected void initSCPPlayerInventory(){
        PlayerInventory inventory = player.getInventory();

        ItemStack blackGlassPane = util.background;
        blackGlassPane.setAmount(1);
        for (int i = 9; i < 36; i++) {
            inventory.setItem(i,blackGlassPane);
        }
        inventory.setItem(4,blackGlassPane);
    }
    //</editor-fold>

    //<editor-fold desc="玩家被伤害">

    public void damaged(Player damager,double value){ //被伤害
        //damager.sendMessage("对玩家" + player.getName() + "造成" + value + "点伤害");

        if(value == -1){ //代码杀
            dead(damager);
        }

        double originalDamage = value;
        double shieldDamage = 0;
        double hpDamage = 0;

        if (currentShield > 0) {
            shieldDamage = Math.min(value, currentShield);
            value -= shieldDamage;
            setCurrentShield(currentShield - shieldDamage);
        }

        hpDamage = value;

        PointAndTime pointAndTime = calculatePoint(hpDamage);
        if(pointAndTime!=null){
            Bukkit.getLogger().warning("损失超过10%的血量, 加分");
            getPoint(damager, pointAndTime);
        }

        setCurrentHp(currentHp - hpDamage);

        if (currentHp <= 0) {
            dead(damager);
        }

        if(roleTemplate.getCamp() == RoleCategory.SCP){ //记录受击时间,暂时不用，scp不能回血
            //onDamaged();
        }
    }
    //</editor-fold>

    //<editor-fold desc="玩家死亡">
    public void dead(Player killer){

        //entity deathMethod
        TeamManager teamManager = TeamManager.getInstance();
        TabManager tabManager = TabManager.getInstance();

        util.clearAllInventory(player);

        if (expUpdateTask != null && !expUpdateTask.isCancelled()) {
            expUpdateTask.cancel();
            expUpdateTask = null;
        }

        player.setHealth(0);

        //add damager's score
//        scoreManager.addScore(damager, this.player);

        //respawn player and handlePlayerData
        Location deathLocation = player.getLocation();
        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
                util.initPlayerDataToSpec(player);
                player.teleport(deathLocation);

                //update TabVisual
                tabManager.updatePlayerRole(player, "spec");

                DistanceChatManager.playerModes.put(player.getUniqueId(), ChatChannel.OBSERVER);

                //remove player'data from dataManager
                teamManager.getAllPlayersMapping().remove(player.getUniqueId());
            }
        }, 5L);
    }

    protected void onDeathCleanup(){}
    //</editor-fold>

    //<editor-fold desc="显示逻辑">
    protected void updateHealthDisplay() {
        updateExpBar();
        updateLevelShield();
    }
    protected void updateExpBar() {
        float healthProgress = (float) (currentHp / maxHp);
        healthProgress = Math.max(0.0f, Math.min(1.0f, healthProgress));
        player.setExp(healthProgress);
    }
    protected void updateLevelShield() {
        int currentShield = (int) Math.ceil(this.currentShield);
        player.setLevel(currentShield);
    }
    //</editor-fold>

    private PointAndTime calculatePoint(double hpDamage) {
        double sourceHp = currentHp;
        double handleHp = Math.max(currentHp - hpDamage, 0);

        if(handleHp==0)return null; //死亡，不得分

        float v = (float)((int) ((sourceHp / maxHp) * 10) ) / 10;
        float v1 = (float) ((int) ((handleHp / maxHp) * 10) ) / 10;

        if(v == v1)return null; //没达到10%不得分

        return Target.getInstance().getDamageSCP();
    }

    protected PointAndTime getDamagePoint() {
        return null;
    }

    protected PointAndTime getKillPoint(){
        return null;
    }

    protected void getPoint(Player player, PointAndTime pointAndTime){
        if(player==null)return;
        if(pointAndTime==null)return;

        Map<UUID, SCPPlayer> allPlayersMapping = TeamManager.getInstance().getAllPlayersMapping();
        RoleCategory sourceCamp = allPlayersMapping.get(player.getUniqueId()).getEntity().getRoleTemplate().getCamp();

        if(sourceCamp==null)return;

        if(sourceCamp==RoleCategory.MTF){
            RespawnSystem.getInstance().getMtf().addPoint(pointAndTime);
            Bukkit.getLogger().warning("MTF" + "加了：" + pointAndTime.getPoint() + "分，减少了：" + pointAndTime.getTime() + "时间");
        }else if(sourceCamp==RoleCategory.CHAOS){
            RespawnSystem.getInstance().getChaos().addPoint(pointAndTime);
            Bukkit.getLogger().warning("chaos" + "加了：" + pointAndTime.getPoint() + "分，减少了：" + pointAndTime.getTime() + "时间");
        }
    }

    public void update(){ // 每tick更新, 显示血量
        if (expUpdateTask != null) {
            expUpdateTask.cancel();
        }

        expUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId());
                if(player.isDead() || !player.isOnline() ||
                scpPlayer==null || (scpPlayer!=null && scpPlayer.getEntity().getRoleTemplate()!=roleTemplate)) {
                    this.cancel();
                    return;
                }

                updateHealthDisplay();
            }
        };
        expUpdateTask.runTaskTimer(SCPMain.getInstance(), 0L, 20L);
    }

    public void setCurrentHp(double currentHp) {
        this.currentHp = Math.min(Math.max(0, currentHp), maxHp);
    }

    public void setCurrentShield(double currentShield){
        this.currentShield = Math.min(Math.max(0, currentShield), maxShield);
    }
}
