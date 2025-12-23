package xyz.xiaocan.scpListener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.xiaocan.scpitemstacks.*;

public class SCPItemListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(event.getPlayer().getItemInHand()));

        if(event.getAction()== Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR){
            if(event.getHand()== EquipmentSlot.HAND){
                if(abstractSCPItem instanceof IOnRightClick onRightClickItem){
                    onRightClickItem.onRightClick(event);
                }
            }
        }else {
            if (abstractSCPItem instanceof IOnLeftClick onLeftClickItem) {
                onLeftClickItem.OnLeftClick(event);
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event){

        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(
                        event.getPlayer().getInventory().getItem(event.getNewSlot())));

        if(abstractSCPItem instanceof IOnSwitchItemBar onSwitchItemBar){   //对下一个进行调用
            onSwitchItemBar.onSwitchItemBar(event, true);
        }

        AbstractSCPItem abstractSCPItem1 = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(
                        event.getPlayer().getInventory().getItem(event.getPreviousSlot())));

        if(abstractSCPItem1 instanceof IOnSwitchItemBar onSwitchItemBar1){  //对上一个也进行调用
            onSwitchItemBar1.onSwitchItemBar(event, false);
        }
    }

    @EventHandler
    public void onPlayerSwapHand(PlayerSwapHandItemsEvent event){
        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(event.getPlayer().getItemInHand()));

        event.setCancelled(true);
        if(abstractSCPItem instanceof IOnSwapHandClick onSwapHandClick){
            onSwapHandClick.onSwapHandClick(event);
        }
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event){
        if(event.getCurrentItem()==null)return;

        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(event.getCurrentItem()));

        if(abstractSCPItem instanceof IOnClickInInventory onClickInInventory){
            onClickInInventory.onClickInInventory(event);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
//        if(event.getItemDrop()==null)return;
        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(event.getItemDrop().getItemStack()));

        if(abstractSCPItem instanceof IOnPlayerDrop onPlayerDrop){
            onPlayerDrop.onPlayerDrop(event);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if((damager instanceof Player player)){
            AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                    .getAllScpItems().get(getIdFromPersistent(player.getInventory().getItemInMainHand()));

            if(abstractSCPItem instanceof IOnPlayerHit onPlayerHit){
                onPlayerHit.OnPlayerHit(event);
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(player.getInventory().getItemInMainHand()));

        if(abstractSCPItem instanceof IOnPlayerSneak onPlayerSneak){
            onPlayerSneak.onPlayerSneak(event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        AbstractSCPItem abstractSCPItem = ItemManager.getInstance()
                .getAllScpItems().get(getIdFromPersistent(player.getInventory().getItemInMainHand()));

        if(abstractSCPItem instanceof IOnPlayerMove onPlayerMove){
            onPlayerMove.onPlayerMove(event);
        }
    }

    public String getIdFromPersistent(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        if (AbstractSCPItem.key == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(AbstractSCPItem.key,
                PersistentDataType.STRING); //这个id用于访问实例
    }
}
