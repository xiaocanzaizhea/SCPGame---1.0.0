package xyz.xiaocan.scpitemstacks.speicalSCPItem;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP207Setting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;
import xyz.xiaocan.tools.progressBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SCP207 extends AbstractSCPItem
        implements IOnRightClick, IOnSwitchItemBar {

    public SCP207Setting scp207;
    public static Map<UUID, Integer> drinkCnt = new HashMap<>();
    public BukkitTask drinkTask;

    public SCP207(SCP207Setting scp207) {
        super(scp207.id, scp207.disPlayName,
                scp207.material, scp207.customModelData, scp207.lore);

        this.scp207 = scp207;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Bukkit.getLogger().warning("进入右键");
        drinkTask = new BukkitRunnable(){
            float t = 0;
            @Override
            public void run() {
                t+=0.05;
                if(t>= scp207.useTime){
                    cancelTask(event.getPlayer());
                    useItem(event.getPlayer());
                    event.getPlayer().playSound(event.getPlayer().getLocation(), //todo 播放可乐使用的声音
                            Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);

                    getPointFromItem(event.getPlayer(),  //为己方阵营加分
                            Target.getInstance().getUseConsumeSCPItem());
                    return;
                }
                progressBar.updateUseProgress(event.getPlayer(),
                        t, scp207.useTime, "scp207使用中");
            }
        }.runTaskTimer(SCPMain.getInstance(),0L,1L);


    }

    public void useItem(Player player){
        drinkCnt.put(player.getUniqueId(),drinkCnt.getOrDefault(player.getUniqueId(),0) + 1);

        float originWalkSpeed = player.getWalkSpeed();
        player.setWalkSpeed(originWalkSpeed +
                originWalkSpeed * scp207.speedUpValue
                        * drinkCnt.getOrDefault(player.getUniqueId(),0));

        player.getInventory().setItemInMainHand(null);  //直接消失
        //todo 开启任务减少玩家hp
        progressBar.sendMessageOnActionBar(player,"scp207饮用完成");
    }

    public void cancelTask(Player player){
        if (drinkTask != null && !drinkTask.isCancelled()) {
            drinkTask.cancel();
        }
        drinkTask=null;

        progressBar.clearUseProgress(player);
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        //todo playsound
        if(check){
            //切换播放声音
        }else{
            //关闭任务
            cancelTask(event.getPlayer());
        }
    }
}
