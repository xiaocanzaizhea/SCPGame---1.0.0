package xyz.xiaocan.scpListener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.configload.option.DoorTemplate;
import xyz.xiaocan.doorsystem.FloorContainmentDoor;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.visual.Menu;
import xyz.xiaocan.visual.StickSelectLocation;
import xyz.xiaocan.visual.Stick;

import java.util.List;


public class DoorListener implements Listener {

    private final DoorManager doorManager;
    private final Stick debugStickContainer;

    public DoorListener() {
        this.doorManager = DoorManager.getInstance();
        this.debugStickContainer = Stick.getInstance();
    }

    @EventHandler
    public void onPlayerClickLock(PlayerInteractAtEntityEvent event){
        if (!(event.getRightClicked() instanceof Interaction)) {
            return;
        }

        Interaction interaction = (Interaction) event.getRightClicked();
        String doorid = interaction.
                getPersistentDataContainer().
                get(FloorContainmentDoor.door_id, PersistentDataType.STRING);

        if(doorid==null){
            return;
        }

        FloorContainmentDoor floorContainmentDoor =
                DoorManager.getInstance().getDoors().get(doorid);

        if(floorContainmentDoor==null)return;

        floorContainmentDoor.toggle();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        //检测开门逻辑
        if(event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK){
            //右键方块或者空气
            FloorContainmentDoor clickedDoor = tryGetTargetDoor(player);

            if(clickedDoor!=null){
                DoorTemplate doorTemplate = clickedDoor.getDoorTemplate();

//                if(!hasAccess(player, clickedDoor)){//没有权限 进行失败的逻辑
//                    player.sendMessage( ChatColor.RED + "权限不足!");
//
//                    try{
//                        Sound sound = doorTemplate.getFailSoundEffect();
//                        player.getLocation().getWorld().playSound(player.getLocation(), sound,
//                                2.0f, 0.7f);
//                    }catch (IllegalArgumentException e){
//                        Bukkit.getLogger().warning("门类型 " + doorTemplate.getId() + " 的声音配置错误: " + doorTemplate.getFailSoundEffect());
//                    }
//                    return;
//                }

                if(!doorTemplate.isCanOpenByPlayer()){
                    return;
                }

                clickedDoor.toggle();

            }else{
                //未查询到碰撞盒id，尝试获取方块
                Block block = event.getClickedBlock();
                if(block != null){

                    clickedDoor = doorManager.isPartOfAnyDoor(block);
                    if(clickedDoor!=null){

                        DoorTemplate doorTemplate = clickedDoor.getDoorTemplate();

                        if(!doorTemplate.isCanOpenByPlayer()){
                            return;
                        }

                        clickedDoor.toggle();
                    }
                }
            }
        }
    }

    public boolean hasAccess(Player player, FloorContainmentDoor door) {
        DoorTemplate doorTemplate = door.getDoorTemplate();
        List<Integer> doorPermissionLevels = doorTemplate.getPermissionsLevel();

        SCPPlayer scpPlayer = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId());
        if(scpPlayer==null)return false;

        GameEntity entity = scpPlayer.getEntity();
        if(entity instanceof SCPEntity){ //scp自带三类权限等级1,0,1
            return 1 >= doorPermissionLevels.get(0) &&
                    0 >= doorPermissionLevels.get(1) &&
                    1 >= doorPermissionLevels.get(2);
        }

        for (int i = 0; i < 9; i++) {
            if(i==4)continue;
            ItemStack item = player.getInventory().getItem(i);
            if (Card.isCard(item)) {
                String cardId = Card.getCardId(item);
                if (cardId == null) {
                    return false;
                }

                Card card = ItemManager.getInstance().getAllCards().get(CardType.valueOf(cardId));
                if (card == null) {
                    player.sendMessage(ChatColor.RED + "无效的门禁卡！" + cardId);
                    return false;
                }

                List<Integer> cardPermissionsLevels = card.getPermissionsLevel();

                return cardPermissionsLevels.get(0) >= doorPermissionLevels.get(0) &&
                        cardPermissionsLevels.get(1) >= doorPermissionLevels.get(1) &&
                        cardPermissionsLevels.get(2) >= doorPermissionLevels.get(2);
            }
        }

        return false;
    }

    //<editor-fold desc="关于调试棒的事件监听">
    @EventHandler
    public void onPlayerClickBlock(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(event.getHand()!= EquipmentSlot.HAND){
            return;
        }

        if(!isStick(event.getPlayer().getInventory().getItemInMainHand())){
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null){
            return;
        }

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            StickSelectLocation.firstLocation = clickedBlock.getLocation();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "第一个点设置成功" + StickSelectLocation.firstLocation);

        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            StickSelectLocation.secondLocation = clickedBlock.getLocation();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "第二个点设置成功" + StickSelectLocation.secondLocation);
        }

    }

    /**
     * 丢弃木棍打开菜单
     * @param event
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        ItemStack itemDrop = event.getItemDrop().getItemStack();
        Player player = event.getPlayer();

        if(!isStick(itemDrop)){
            return;
        }

        event.setCancelled(true);

        Menu main = debugStickContainer.tryGetMenu("main");  //尝试获取主菜单进入
        if(main==null){
            player.sendMessage("§c菜单未初始化，请联系管理员");
            return;
        }

        player.sendMessage(ChatColor.BLUE + "MenuSize: " + ChatColor.GRAY + Menu.values().length);
        player.openInventory(main.getInventory());
    }

    /**
     * 这个事件处理我们在菜单点击的操作
     */
    @EventHandler
    public void onPlayerClickInInventory(InventoryClickEvent event){

        Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory==null){
            return;
        }

        Menu menu = debugStickContainer.tryGetMenu(clickedInventory);
        if(menu==null){ //菜单列表不包含这个菜单
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

//        player.sendMessage(menu.getName());
        debugStickContainer.handleMenuClick(player, menu, slot);   //处理打开菜单逻辑
    }
    //</editor-fold>

    public boolean isStick(ItemStack item){
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "调试棒(๑•̀ㅂ•́)و✧");
    }

    /**
     * 获取范围以寻找碰撞盒
     */
    private FloorContainmentDoor tryGetTargetDoor(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        double step = 0.1f;
        double total = 3f;
        int steps = (int) (total / step);

        for (int i = 0; i <= steps; i++) {
            double distance = step * i;
            Location checkLocation = eyeLocation.clone().add(direction.clone().multiply(distance));

            String id = doorManager.isInBoundingBox(checkLocation);
            if(id != null){
                return doorManager.idGetDoor(id);
            }
        }

        return null;
    }

}
