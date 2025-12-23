package xyz.xiaocan.scpitemstacks.card.snake;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public enum Key {
    UP(createKey("up")),
    DOWN(createKey("down")),
    LEFT(createKey("left")),
    RIGHT(createKey("right"));

    Key(ItemStack key) {
        this.item = key;
    }

    private ItemStack item;

    private static ItemStack createKey(String displayName){
        ItemStack itemStack = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
