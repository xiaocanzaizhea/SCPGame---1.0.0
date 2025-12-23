package xyz.xiaocan.scpitemstacks.speicalSCPItem;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.chatsystem.ChatChannel;
import xyz.xiaocan.chatsystem.DistanceChatManager;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP1576Setting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnPlayerDrop;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;
import xyz.xiaocan.tools.progressBar;
import xyz.xiaocan.tools.util;

public class SCP1576 extends AbstractSCPItem
        implements IOnRightClick, IOnSwitchItemBar, IOnPlayerDrop {

    public long lastUseTime = -1;
    public BukkitTask useTask; //代表使用的时间任务
    public BukkitTask usingTask; //正在使用的任务
    public SCP1576Setting scp1576;
    private boolean isFirstUse = true;
    public SCP1576(SCP1576Setting scp1576) {
        super(scp1576.id, scp1576.displayName,
                scp1576.material, scp1576.customModelData, scp1576.lore);

        this.scp1576 = scp1576;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        if(!canUse())return;

        lastUseTime = System.currentTimeMillis();
        useTask = new BukkitRunnable(){
            float t = 0;
            @Override
            public void run() {
                t+=0.05;
                if(t>= scp1576.usageTime){ //停下使用任务
                    cancelTask(event.getPlayer(), useTask);
                    useItem(event.getPlayer());
                    event.getPlayer().playSound(event.getPlayer().getLocation(),
                            Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);

                    if(isFirstUse){
                        getPointFromItem(event.getPlayer(),  //为己方阵营加分
                                Target.getInstance().getFirstUseSCPItem());
                        isFirstUse = false;
                    }
                    return;
                }
                progressBar.updateUseProgress(event.getPlayer(),
                        t, scp1576.getUsageTime(), "scp1576使用中");
            }
        }.runTaskTimer(SCPMain.getInstance(),0L,1L);
    }

    private void useItem(Player player){
        DistanceChatManager
                .setPlayerChatMode(player, ChatChannel.OBSERVER); //先设置为观察者频道

        usingTask = new BukkitRunnable(){
            float t=scp1576.talkTime;

            @Override
            public void run() {
                t-=0.05;
                if(t<=0){ //使用时间结束
                    cancelUsingTask(player);
                    return;
                }

                progressBar.updateUseProgress(player,
                        t,scp1576.talkTime,"scp1576剩余使用时间");
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,1l);
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){//playsound

        }else{//canceltask
            cancelTask(event.getPlayer(), useTask);
            cancelUsingTask(event.getPlayer());
        }
    }

    @Override
    public void onPlayerDrop(PlayerDropItemEvent event) {
        cancelTask(event.getPlayer(), useTask);
        cancelUsingTask(event.getPlayer());
    }

    private boolean canUse(){
        long currentTime = System.currentTimeMillis();
        boolean check = useTask!=null && !useTask.isCancelled(); //运行中
        boolean cd = util.isOnCooldown(currentTime,
                lastUseTime,(long)(scp1576.cd * 1000));
        return !cd && !check;
    }

    private void cancelUsingTask(Player player){
        cancelTask(player,usingTask);

        DistanceChatManager
                .setPlayerChatMode(player,ChatChannel.LOCAL);
    }
}
