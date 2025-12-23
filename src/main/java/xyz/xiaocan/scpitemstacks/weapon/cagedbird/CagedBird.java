package xyz.xiaocan.scpitemstacks.weapon.cagedbird;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import xyz.xiaocan.configload.option.itemoption.cagebrid.CagedBirdSetting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.*;
import xyz.xiaocan.tools.progressBar;
import xyz.xiaocan.tools.util;

import java.util.UUID;

public class CagedBird extends AbstractSCPItem
        implements IOnRightClick, IOnLeftClick, IOnSwitchItemBar, IOnPlayerDrop, IOnPlayerDamage {

    public boolean canChargeAttack;

    CagedBirdSetting cagedBirdSetting;

    public int remainUseCount;
    public long lastNormalAttck;
    public long lastChargeAttack;

    public BukkitTask chargeTask;
    public BukkitTask attackTask;

    public CagedBird(CagedBirdSetting cagebird){
        super(UUID.randomUUID().toString(), cagebird.displayName,
                cagebird.material, cagebird.customModelData, cagebird.lore);

        cagedBirdSetting = cagebird;
        canChargeAttack = false;
        remainUseCount = cagebird.MaxUseCount;
        lastNormalAttck = -1;
        lastChargeAttack = -1;
    }


    public void setItemModel(ItemStack itemStack, int id){
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(id);
        itemStack.setItemMeta(meta);
    }

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        if(event.getAction()== Action.RIGHT_CLICK_BLOCK
                || event.getAction()==Action.RIGHT_CLICK_AIR){

            Player player = event.getPlayer();
            ItemStack cagebirdItem = player.getInventory().getItemInMainHand();

            if(!canChargeAttack){ //蓄力阶段
                //蓄力
                Bukkit.getLogger().warning("进入囚鸟蓄力点击");

                if(chargeTask==null){
                    chargeTask = new BukkitRunnable(){
                        float t=0;

                        @Override
                        public void run() {
                            if((t+=0.05)>=cagedBirdSetting.getChargeTime()){
                                this.cancel();
                                canChargeAttack = true;
                                chargeTask = null;
                                progressBar.sendMessageOnActionBar(player, ChatColor.GREEN + "蓄力完成");
                                setItemModel(cagebirdItem, 11);
                                return;
                            }

                            progressBar.updateUseProgress(player, t, (float) cagedBirdSetting.getChargeTime(),"囚鸟充能中");
                        }
                    }.runTaskTimer(SCPMain.getInstance(),0l,1l);
                }

            }else { //攻击阶段
                Bukkit.getLogger().warning("进入囚鸟冲刺阶段");

                if(attackTask==null){
                    attackTask = new BukkitRunnable(){
                        float t=0;

                        @Override
                        public void run() { //任务结束
                            if((t+=0.05)>=cagedBirdSetting.chargeAttackTime ||
                                    !canChargeAttack){
                                this.cancel();
                                handleTaskinitAndItemChange(cagebirdItem);
                                progressBar.sendMessageOnActionBar(player,ChatColor.RED + "结束冲刺");
                                return;
                            }

                            player.setVelocity(player.getEyeLocation()
                                    .getDirection().clone().setY(0).normalize()
                                    .multiply(cagedBirdSetting.chargeAttackSpeed));

                            Location checkLoc = player.getLocation().
                                    add(player.getEyeLocation().getDirection().
                                            multiply(cagedBirdSetting.chargeAttackDistance));

                            double detectionRadius = 2;
                            BoundingBox detectionBox = BoundingBox.of(checkLoc, detectionRadius, detectionRadius, detectionRadius);

                            for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                                if (!nearbyPlayer.equals(player) &&
                                        detectionBox.contains(nearbyPlayer.getLocation().toVector())) {

                                    remainUseCount-=1;
                                    // 找到敌人，处理攻击逻辑
                                    util.PlayerDamaged(player, nearbyPlayer, cagedBirdSetting.chargeDamage);

                                    handleTaskinitAndItemChange(cagebirdItem);

                                    if(remainUseCount<0){ //用完后报废
                                        player.sendMessage("囚鸟已经报废");
                                        player.getInventory().setItemInMainHand(null); //
                                        return;
                                    }
                                    break;
                                }
                            }
                        }
                    }.runTaskTimer(SCPMain.getInstance(),0l,1l);
                }
            }
        }
    }

    @Override
    public void onPlayerDrop(PlayerDropItemEvent event) {
        cancelCagedBirdTasks();
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {//处理他独有的切换物品的声音等
        if(check){
            ItemStack previousItem = event.getPlayer().getInventory()
                    .getItem(event.getPreviousSlot());

            if(canChargeAttack==true && remainUseCount!=1){
                setItemModel(previousItem,10);
            }
        }else{
            cancelCagedBirdTasks();
        }
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getDamageSource().getCausingEntity() instanceof Player source) ||
                !(event.getEntity() instanceof Player target)) {
            return;
        }

        if(!canChargeAttack){
            long currentTime = System.currentTimeMillis();
            long cooldown = (long) (cagedBirdSetting.normalAttckCD * 1000);
            if (util.isOnCooldown(currentTime, lastNormalAttck, cooldown)) {
                return;
            }

            lastNormalAttck = currentTime;
            util.PlayerDamaged(source, target, cagedBirdSetting.normalDamage);
        }
    }

    private void handleTaskinitAndItemChange(ItemStack itemStack){
        canChargeAttack = false;
        attackTask=null;
        if(remainUseCount==1){
            setItemModel(itemStack,12);
        }else{
            setItemModel(itemStack,10);
        }
    }

    private void cancelCagedBirdTasks() {
        if (chargeTask != null
                && !chargeTask.isCancelled()) {
            chargeTask.cancel();
            chargeTask = null;
        }

        if (attackTask != null
                && !attackTask.isCancelled()) {
            attackTask.cancel();
            attackTask = null;
        }

        canChargeAttack = false;
    }
}
