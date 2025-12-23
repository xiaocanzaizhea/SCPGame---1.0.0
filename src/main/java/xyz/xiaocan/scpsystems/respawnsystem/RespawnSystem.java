package xyz.xiaocan.scpsystems.respawnsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.RifleMan;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.Chaos;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.MTF;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.HumanType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
/**
 * 负责玩家重生为九尾狐和混沌
 */
public class RespawnSystem {
    private static RespawnSystem instance;

    private MTF mtf;
    private Chaos chaos;

    private int maxTokenGet;
    private RespawnSystem() {
        this.mtf = MTF.getInstance();
        this.chaos = Chaos.getInstance();

        maxTokenGet = 3;
    }

    public void timerStart(){ //游戏开始时调用
        //有令牌的一方优先重生
        //当两者令牌数一样，优先重生九尾狐
        //会暂停混沌刷新时间,并且bossbar变为灰色
        if(mtf.getTokenCnt()>=chaos.getTokenCnt()){ //九尾狐优先重生
            mtf.subTime();
            if(mtf.getRespawnTime()>30){
                chaos.subTime();
            }else{
                chaos.getBossBar().setColor(BarColor.WHITE);
            }
        }else if(mtf.getTokenCnt()<chaos.getTokenCnt()){ //混沌优先
            chaos.subTime();
            if(chaos.getRespawnTime()>30){
                mtf.subTime();
            }else{
                mtf.getBossBar().setColor(BarColor.WHITE);
            }
        }


        if(mtf.getRespawnTime()<=0 && mtf.getTokenCnt()>0){ //重生大波
            setCampToDefault(300);
            mtf.setTokenCnt(mtf.getTokenCnt()-1);
            respawnPlayer(true,true);
            return;
        }else{  //小波

        }

        if(chaos.getRespawnTime()<=0 && chaos.getTokenCnt()>0){
            setCampToDefault(300);
            chaos.setTokenCnt(chaos.getTokenCnt()-1);
            respawnPlayer(true,false);
            return;
        }

        //---------------重生为小波次---------------
    }

    public void setCampToDefault(int time){
        mtf.setRespawnTime(time);
        mtf.getBossBar().setColor(mtf.getBarColor());

        chaos.setRespawnTime(time);
        chaos.getBossBar().setColor(chaos.getBarColor());
    }
    public void addBarToPlayer(Player player){
        mtf.addBarToPlayer(player);
        chaos.addBarToPlayer(player);
    }
    public void removeBarFromPlayer(Player player){
        mtf.removeBarFromPlayer(player);
        chaos.removeBarFromPlayer(player);
    }
    private int getBigSupportSpawnCnt(){
        int size = Bukkit.getOnlinePlayers().size();
        return (int) Math.ceil(size * 0.75);
    }
    private int getSmallSupportSpawnCnt(){
        int size = Bukkit.getOnlinePlayers().size();
        return (int) Math.ceil(size / 5);
    }

    //<editor-fold desc="生成九尾或混沌">
    //控制生成九尾或混沌，大波或小波
    public void respawnPlayer(boolean isBigSupprot, boolean isMTF){
        List<? extends Player> list = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList());

        Collections.shuffle(list);

        if(isBigSupprot){
            int bigSupportSpawnCnt = getBigSupportSpawnCnt();
            if(isMTF){
                respawnMTF(bigSupportSpawnCnt, list);
            }else{
                respawnChaos(bigSupportSpawnCnt,list);
            }
        }else{
            int smallSupportSpawnCnt = getSmallSupportSpawnCnt();
            if(isMTF){
                respawnMTF(smallSupportSpawnCnt, list);
            }else{
                respawnChaos(smallSupportSpawnCnt,list);
            }
        }

    }
    private void respawnMTF(int spawnCnt, List<? extends Player> playerList){
        int captainCnt = 1;
        int remaining = spawnCnt - captainCnt;

        int sergeantCnt = (int) (remaining * 0.2);
        int privateCnt = remaining - sergeantCnt;

        int playerIndex = 0;

        for (int i = 0; i < captainCnt && playerIndex < playerList.size(); i++, playerIndex++) {
            Player player = playerList.get(playerIndex);
            RoleTemplate roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.MTFCAPTAIN);
            if (roleTemplate != null) {
                new SCPPlayer(player, new RifleMan(player, roleTemplate), HumanType.MTFCAPTAIN);
                Bukkit.getLogger().info("分配队长给: " + player.getName());
            } else {
                Bukkit.getLogger().warning("队长模板不存在！");
            }
        }

        for (int i = 0; i < sergeantCnt && playerIndex < playerList.size(); i++, playerIndex++) {
            Player player = playerList.get(playerIndex);
            RoleTemplate roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.MTFPSERGEANT);
            if (roleTemplate != null) {
                new SCPPlayer(player, new RifleMan(player, roleTemplate), HumanType.MTFPSERGEANT);
                Bukkit.getLogger().info("分配中士给: " + player.getName());
            } else {
                Bukkit.getLogger().warning("中士模板不存在！");
            }
        }

        for (int i = 0; i < privateCnt && playerIndex < playerList.size(); i++, playerIndex++) {
            Player player = playerList.get(playerIndex);
            RoleTemplate roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.MTFPRIVATE);
            if (roleTemplate != null) {
                new SCPPlayer(player, new RifleMan(player, roleTemplate), HumanType.MTFPRIVATE);
                Bukkit.getLogger().info("分配列兵给: " + player.getName());
            } else {
                Bukkit.getLogger().warning("列兵模板不存在！");
            }
        }
    }
    private void respawnChaos(int spawnCnt, List<? extends Player> playerList){
        if (playerList.size() < spawnCnt) {
            spawnCnt = playerList.size();
        }

        int rifleManCnt, predatorCnt, suppressorCnt;

        if (spawnCnt <= 3) {
            rifleManCnt = spawnCnt;
            predatorCnt = 0;
            suppressorCnt = 0;
        } else {
            rifleManCnt = (int) (spawnCnt * 0.5);
            predatorCnt = (int) (spawnCnt * 0.3);
            suppressorCnt = spawnCnt - rifleManCnt - predatorCnt;
        }
        int playerIndex = 0;

        for (int i = 0; i < rifleManCnt && playerIndex < playerList.size(); i++, playerIndex++) {
            assignChaosRole(playerList.get(playerIndex), HumanType.RIFLEMAN);
        }

        for (int i = 0; i < predatorCnt && playerIndex < playerList.size(); i++, playerIndex++) {
            assignChaosRole(playerList.get(playerIndex), HumanType.PREDATOR);
        }

        for (int i = 0; i < suppressorCnt && playerIndex < playerList.size(); i++, playerIndex++) {
            assignChaosRole(playerList.get(playerIndex), HumanType.SUPPRESSOR);
        }
    }

    private void assignChaosRole(Player player, HumanType roleType) {
        RoleTemplate roleTemplate = TeamManager.getInstance().getRolesTemplates().get(roleType);
        if (roleTemplate != null) {
            new SCPPlayer(player, new RifleMan(player, roleTemplate), roleType);
            Bukkit.getLogger().info("分配" + roleType + "给: " + player.getName());
        } else {
            Bukkit.getLogger().warning(roleType + "模板不存在！");
            RoleTemplate defaultTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.RIFLEMAN);
            if (defaultTemplate != null) {
                new SCPPlayer(player, new RifleMan(player, defaultTemplate), HumanType.RIFLEMAN);
            }
        }
    }
    //</editor-fold>
    public static RespawnSystem getInstance(){
        if(instance==null) instance = new RespawnSystem();
        return instance;
    }
}
