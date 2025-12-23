package xyz.xiaocan.scpitemstacks.medical.absClass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnPlayerDrop;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.progressBar;

import java.util.List;

public abstract class MedicalSCPItem
        extends AbstractSCPItem
        implements IOnRightClick, IOnSwitchItemBar, IOnPlayerDrop {

    protected double usageTime;
    protected double duringTime;  //没有持续时间就写一秒
    protected double healingHp;
    protected double healingShield;
    protected BukkitTask healTask; //治疗任务

    protected MedicalSCPItem(String id, String disPlayName,
                          double usageTime, double duringTime,
                          double healingHp, double healingShield, Material material,
                          int customModelData, List<String> lore) {
        super(id, disPlayName, material, customModelData, lore);
        this.usageTime = usageTime;
        this.duringTime = duringTime;
        this.healingHp = healingHp;
        this.healingShield = healingShield;
        this.material = material;
        this.lore = lore;
        this.healTask = null;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {  //监听器调用这个
        if(healTask!=null && !healTask.isCancelled())return;

        Player player = event.getPlayer();
        SCPPlayer scpPlayer = TeamManager.getInstance().getSCPPlayer(player);

        if(scpPlayer!=null && scpPlayer.getEntity() instanceof Human human){
            healTask = new BukkitRunnable(){
                float t=0;

                @Override
                public void run() {
                    if((t+=0.05)>=usageTime){
                        heal(human);
                        player.getInventory().setItemInMainHand(null);
                        player.playSound(player.getLocation(),
                                Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
                        cancelTask(player,healTask);
                        return;
                    }

                    progressBar.updateUseProgress(player,
                            t, (float) usageTime,
                            ChatColor.GREEN + disPlayName + "使用中");

                }
            }.runTaskTimer(SCPMain.getInstance(), 0l, 1l);
        }
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check){
        if(check){ //playEquipSound

        }else{ //cancelTask
            cancelTask(event.getPlayer(), healTask);
        }
    }

    @Override
    public void onPlayerDrop(PlayerDropItemEvent event){
        cancelTask(event.getPlayer(), healTask);
    }

    protected void heal(Human human){
        healHp(human);
        healShield(human);
    }

    protected void healHp(Human human){
        new BukkitRunnable(){
            float temp = 0;
            @Override
            public void run() {
                temp++;
                human.setCurrentHp(human.getCurrentHp() + (double)healingHp / (double) duringTime);
                if(temp>=duringTime){
                    this.cancel();
                }
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,20l);
    }

    protected void healShield(Human human){
        human.setCurrentShield(human.getCurrentShield() + healingShield);
        Bukkit.getLogger().warning("治疗玩家护盾" + human.getCurrentShield() + healingShield);
    }

}
