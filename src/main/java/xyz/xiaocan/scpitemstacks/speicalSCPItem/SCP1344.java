package xyz.xiaocan.scpitemstacks.speicalSCPItem;

import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP1344Setting;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.RifleMan;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.DCLASS;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.Guard;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.MTFPrivate;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.Scientist;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnPlayerDrop;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.progressBar;

public class SCP1344 extends AbstractSCPItem
        implements IOnRightClick, IOnPlayerDrop, IOnSwitchItemBar {

    private SCP1344Setting scp1344;
    private BukkitTask task;
    private BukkitTask usingTask;

    private boolean isFirstUse = true;
    public SCP1344(SCP1344Setting scp1344) {
        super(scp1344.id, scp1344.displayName,
                scp1344.material, scp1344.customModelData, scp1344.lore);

        this.scp1344 = scp1344;
    }

    @Override
    public void onPlayerDrop(PlayerDropItemEvent event) {
        //持续掉血直至死亡
        new BukkitRunnable(){

            @Override
            public void run() {
                if(event.getPlayer().isDead()){
                    this.cancel();
                }

                SCPPlayer scpPlayer = TeamManager.getInstance().getSCPPlayer(event.getPlayer());
                if(scpPlayer==null){
                    event.getPlayer().sendMessage("你脱下了眼镜");
                    return;
                }

                scpPlayer.getEntity().damaged(null,15.0); //每秒掉15滴血
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,20l);
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        boolean check = task!=null && !task.isCancelled(); //正在运行中
        if(check)return;

        task = new BukkitRunnable(){
            float t=0;
            @Override
            public void run() {
                t+=0.05;
                if(t>=scp1344.usageTime){
                    cancelTask(event.getPlayer(), task);
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
                        t, scp1344.getUsageTime(), "scp1576使用中");
            }
        }.runTaskTimer(SCPMain.getInstance(), 0l,1l);
    }

    private void useItem(Player player){//为这个玩家显示所有存活玩家的标志

        //开一个usingTask
        usingTask = new BukkitRunnable(){

            @Override
            public void run() {
                if(player.isDead()){
                    cancelTask(player,usingTask);
                }

                Bukkit.getOnlinePlayers().forEach(p -> {
                    ChatColor color = CheckColor(p);
                    if (color == null) return;

                    Location spawnLoc = p.getEyeLocation().add(0, 0.5, 0);
                    TextDisplay textDisplay = spawnLoc.getWorld().spawn(spawnLoc, TextDisplay.class);

                    textDisplay.setText(color + "●");
                    textDisplay.setDisplayWidth(1);
                    textDisplay.setDisplayHeight(1);
                    textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                    textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
                    textDisplay.setSeeThrough(true);
                    textDisplay.setBillboard(Display.Billboard.CENTER);

                    Bukkit.getOnlinePlayers() //对所有玩家隐藏，除了player
                            .stream()
                            .filter(p1 -> p==player)
                            .forEach(player1 -> {
                                player1.hideEntity(SCPMain.getInstance(), textDisplay);
                            });

                    Bukkit.getScheduler()
                            .runTaskLater(SCPMain.getInstance(), textDisplay::remove, 50l);
                });
            }
        }.runTaskTimer(SCPMain.getInstance(),0L,5l);


    }

    public ChatColor CheckColor(Player player){
        SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer==null)return null;

        GameEntity entity = scpPlayer.getEntity();
        if(entity instanceof SCPEntity){
            return ChatColor.DARK_RED;
        }else if(entity instanceof RifleMan){
            return ChatColor.DARK_GREEN;
        }else if(entity instanceof MTFPrivate){
            return ChatColor.BLUE;
        }else if(entity instanceof DCLASS){
            return ChatColor.YELLOW;
        }else if(entity instanceof Guard){
            return ChatColor.BLUE;
        }else if(entity instanceof Scientist){
            return ChatColor.WHITE;
        }else {
            return ChatColor.LIGHT_PURPLE; //错误类型
        }
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){ //播放声音

        }else{
            cancelTask(event.getPlayer(), task);
        }
    }
}
