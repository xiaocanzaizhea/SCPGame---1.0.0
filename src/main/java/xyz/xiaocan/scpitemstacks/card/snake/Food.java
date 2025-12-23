package xyz.xiaocan.scpitemstacks.card.snake;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public class Food {
    private static Food instance;
    private ItemStack food;

    private Food(){
        this.food = createFood();
    }

    private static ItemStack createFood(){
        ItemStack head = new ItemStack(Material.EGG.asItemType().createItemStack());
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName("食物");
        head.setItemMeta(meta);
        return head;
    }

    public static Food getInstance(){
        if(instance==null){
            instance = new Food();
        }
        return instance;

    }
}
