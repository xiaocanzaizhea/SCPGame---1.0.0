package xyz.xiaocan.scpitemstacks.speicalSCPItem;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP268Setting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;
import xyz.xiaocan.tools.progressBar;
import xyz.xiaocan.tools.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SCP268 extends AbstractSCPItem implements IOnRightClick, IOnSwitchItemBar {

    //todo,隐身帽是根据玩家计算冷却的，不是实例
    public Map<UUID, Float> allPlayersLastUseTime = new HashMap<>();

    public long lastUseTime=-1;
    public BukkitTask useTask;
    private boolean isFirstUse = true;

    SCP268Setting scp268;

    public SCP268(SCP268Setting scp268Setting) {
        super(scp268Setting.id,
                scp268Setting.displayName,
                scp268Setting.material,
                scp268Setting.customModelData,
                scp268Setting.lore);

        this.scp268 = scp268Setting;
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event,boolean check) {
        if(check){
//            Player player = event.getPlayer();
//            player.getWorld().playSound(
//                    player.getLocation(),
//                    String.valueOf(NamespacedKey
//                            .fromString(scp268.equipSound)),
//                    SoundCategory.PLAYERS,
//                    1.5f,
//                    1.2f
//            );
        }else{ //cancelTask
            cancelTask(event.getPlayer());
        }
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
                if(t>= scp268.usageTime){
                    cancelTask(event.getPlayer());
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
                        t, scp268.getUsageTime(), "scp268使用中");
            }
        }.runTaskTimer(SCPMain.getInstance(),0L,1L);


    }

    private boolean canUse(){
        long currentTime = System.currentTimeMillis();
        boolean check = useTask!=null && !useTask.isCancelled(); //运行中
        boolean cd = util.isOnCooldown(currentTime,
                lastUseTime,(long)(scp268.cd * 1000));
        return !cd && !check;
    }

    public void useItem(Player player){
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.equals(player)) {
                p.hidePlayer(SCPMain.getInstance(), player);
            }
        });

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*60, 1, true, false));

        player.sendMessage("§a你已隐身！任何交互都会打断效果。");

        Listener interruptListener = new Listener() {
            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                if (event.getPlayer().equals(player)) {
                    interruptEffect(player, this);
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked().equals(player)) {
                    interruptEffect(player, this);
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onPlayerDamage(EntityDamageEvent event) {
                if (event.getEntity().equals(player)) {
                    interruptEffect(player, this);
                }
            }

            @EventHandler
            public void onPlayerAttack(EntityDamageByEntityEvent event) {
                if (event.getDamager().equals(player)) {
                    interruptEffect(player, this);
                }
            }
        };

        Bukkit.getPluginManager()
                .registerEvents(interruptListener, SCPMain.getInstance());

        player.setMetadata("invisibility_listener",
                new FixedMetadataValue(SCPMain.getInstance(), interruptListener));
    }

    public void cancelTask(Player player){
        if (useTask != null && !useTask.isCancelled()) {
            useTask.cancel();
        }
        useTask=null;

        progressBar.clearUseProgress(player);
    }

    private void interruptEffect(Player player, Listener listener) {
        HandlerList.unregisterAll(listener);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.equals(player)) {
                p.showPlayer(SCPMain.getInstance(), player);
            }
        });

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.GLOWING);

        player.removeMetadata("invisibility_listener", SCPMain.getInstance());

        player.sendMessage("§c隐身效果被打断了！");
    }
}
