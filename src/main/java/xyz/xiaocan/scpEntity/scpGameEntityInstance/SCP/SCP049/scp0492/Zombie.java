package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049.scp0492;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.configload.option.scpoption.SCP0492SpecialSetting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnLeftClick;
import xyz.xiaocan.scpitemstacks.IOnPlayerHit;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.util;

import java.util.List;

public class Zombie extends AbstractSCPItem
        implements IOnPlayerHit, IOnLeftClick {

    public SCP0492SpecialSetting scp0492SpecialSetting = SCP0492SpecialSetting.getInstance();

    private long lastLeftHit = -1;
    private Player player;

    private int speedAddLayers;

    private long lastGazePlayerTime = -1;

    public Zombie(String id, String disPlayName,
                  Material material, int customModelData, List<String> lore, Player player) {
        super(id, disPlayName, material, customModelData, lore);

        this.player = player;
        createPlayerGazeTask();
    }

    @Override
    public void OnPlayerHit(EntityDamageByEntityEvent event) {
        long currentTime = System.currentTimeMillis();
        if(util.isOnCooldown(currentTime,
                lastLeftHit, (long) (scp0492SpecialSetting.getCd() * 1000) ))return;

        if(event.getEntity() instanceof Player target && event.getDamager() instanceof Player source){
            SCPPlayer scpPlayer = TeamManager.getInstance().getSCPPlayer(target);
            if(scpPlayer!=null){
                scpPlayer.getEntity().damaged(source, scp0492SpecialSetting.getDamage());
            }
        }
    }

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {
        long currentTime = System.currentTimeMillis();
        lastLeftHit = currentTime;
    }

    private void createPlayerGazeTask() {
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!player.isOnline() || player.isDead()){
                    this.cancel();
                }

                long currentTime = System.currentTimeMillis();
                Player target = util.findGazeScp0492(player);
                if(target!=null){
                    lastGazePlayerTime = currentTime;
                    if(speedAddLayers< scp0492SpecialSetting.getMaxSpeedAddLayers()){
                        speedAddLayers++;
                        player.setWalkSpeed(player.getWalkSpeed() + scp0492SpecialSetting.getSpeedAddCnt());
                    }
                }

                if(util.isOnCooldown(currentTime, lastGazePlayerTime, 5000)){ //5秒未看见人类
                    if(speedAddLayers>0){
                        speedAddLayers--;
                        player.setWalkSpeed(player.getWalkSpeed() - scp0492SpecialSetting.getSpeedSubCnt());
                    }
                }
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,20l);
    }
}
