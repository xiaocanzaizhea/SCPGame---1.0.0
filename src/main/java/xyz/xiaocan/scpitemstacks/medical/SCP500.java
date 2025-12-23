package xyz.xiaocan.scpitemstacks.medical;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.Medical;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.medical.absClass.MedicalSCPItem;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.progressBar;

public class SCP500 extends MedicalSCPItem{

    public SCP500(Medical medical) {
        super(medical.id, medical.displayName, medical.usageTime,
                medical.duringTime, medical.healHp, medical.healShield,
                medical.material, medical.customModelData, medical.lore);
    }
    @Override
    public void heal(Human human) {
        super.heal(human);

        //todo
        //scp500特殊处理,给与玩家10秒最高100的生命恢复
    }

    @Override
    public void onRightClick(PlayerInteractEvent event){
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

                        getPointFromItem(player,
                                Target.getInstance().getUseConsumeSCPItem());

                        return;
                    }

                    progressBar.updateUseProgress(player,
                            t, (float) usageTime,
                            ChatColor.GREEN + disPlayName + "使用中");

                }
            }.runTaskTimer(SCPMain.getInstance(), 0l, 1l);
        }
    }
}
