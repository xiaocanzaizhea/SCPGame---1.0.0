package xyz.xiaocan.scpitemstacks.terrainSCP.scp914;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SCP914Model {
    Rough("粗加工", 0,9141),
    HalfRough("半粗加工", 1,9142),
    Oneone("1比1加工", 2,9143),
    Fine("精加工", 3,9144),
    VeryFine("超精加工", 4,9145);

    public String id;
    public ItemStack itemStack;
    public int num;
    public int custommodeldata;

    SCP914Model(String id, int num, int custommodeldata){
        this.id = id;
        this.num = num;
        this.custommodeldata = custommodeldata;
        this.itemStack = createItemWithCustomModelData(Material.COAL, custommodeldata);
    }

    private ItemStack createItemWithCustomModelData(Material material, int customModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
        }
        return item;
    }
}
