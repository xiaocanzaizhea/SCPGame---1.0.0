package xyz.xiaocan.scpitemstacks.card.snake;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
public class Snake {
    //--这部分表示蛇整体--
    private ItemInInventory head;
    private Queue<ItemInInventory> body;  //维护这个列表

    private Key moveDirection; //只使用x,y

    //--无关的部分--
    private final ItemStack headItem = createHead();
    private final ItemStack tailItem = createTail();

    public Snake(int headSlot, Key moveDirection) {
        this.head = new ItemInInventory(headItem, headSlot);
        body = new LinkedList<>();
        body.add(new ItemInInventory(tailItem, 0));
        this.moveDirection = moveDirection;
    }

    private ItemStack createHead(){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD.asItemType().createItemStack());
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName("蛇头");
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createTail(){
        ItemStack head = new ItemStack(Material.GREEN_WOOL.asItemType().createItemStack());
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName("蛇尾");
        head.setItemMeta(meta);
        return head;
    }
}
