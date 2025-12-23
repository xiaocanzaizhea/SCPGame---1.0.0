package xyz.xiaocan.scpitemstacks.terrainSCP.scp330;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Candy {
    Purple(0),
    Red(1),
    Blue(2),
    Green(3),
    Yellow(4),
    Rainbow(5);
    public int customModelData;
    public ItemStack itemStack;

    Candy(int customModelData) {
        this.customModelData = customModelData;
        this.itemStack = createItemStack();
    }

    private ItemStack createItemStack() {
        ItemStack itemStack1 = new ItemStack(Material.COAL);
        ItemMeta meta = itemStack1.getItemMeta();
        meta.setCustomModelData(customModelData);
        itemStack1.setItemMeta(meta);
        return itemStack1;
    }
}
