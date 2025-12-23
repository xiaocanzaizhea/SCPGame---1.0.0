package xyz.xiaocan.scpitemstacks.weapon.gun.absClass;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xiaocan.configload.option.itemoption.gun.Ammo;
import xyz.xiaocan.dropitemsystem.DropManager;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.IOnClickInInventory;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;

import java.util.Map;

public class AmmoSCPItem extends AbstractSCPItem implements IOnClickInInventory {
    public int maxAmmoTake;
    public AmmoSCPItem(Ammo ammo) {
        super(ammo.id, ammo.displayName,
                ammo.material, ammo.customModelData, ammo.lore);

        this.maxAmmoTake = ammo.maxAmmoTake;
    }

    @Override
    public void onClickInInventory(InventoryClickEvent event) {//在背包被点击
        ClickType click = event.getClick();
        ItemStack currentItem = event.getCurrentItem();

        if(event.getWhoClicked() instanceof Player player){
            if(click==ClickType.LEFT){//丢弃最多10个
                createDropItemAndUpdateInventory(currentItem,
                        10,player, AmmoType.valueOf(id));
            }else if(click==ClickType.RIGHT){ //最多丢弃40个
                createDropItemAndUpdateInventory(currentItem,
                        40,player, AmmoType.valueOf(id));
            }
        }
    }

    public void createDropItemAndUpdateInventory(ItemStack currentItem, int maxDropCount, Player player, AmmoType ammoType){
        ItemManager itemManager = ItemManager.getInstance();
        Map<AmmoType, Integer> ammoTypeIntegerMap = itemManager
                .allPlayersAmmo
                .get(player.getUniqueId());
        Integer orDefault = ammoTypeIntegerMap.getOrDefault(ammoType, 0);
        int dropCnt = Math.min(maxDropCount, orDefault);

        ItemStack dropItem = new ItemStack(currentItem.getType());
        dropItem.setAmount(dropCnt);

        DropManager.getInstance().createDropItem(dropItem,player.getLocation()); //创建掉落物
        ammoTypeIntegerMap.put(ammoType,orDefault - dropCnt); //改变内部数据

        itemManager.updatePlayerInventoryAmmo(player);//更新可视化
    }
}
