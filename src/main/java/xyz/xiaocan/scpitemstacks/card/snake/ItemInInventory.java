package xyz.xiaocan.scpitemstacks.card.snake;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class ItemInInventory {
    private ItemStack part;
    private int slot;

    public ItemInInventory(ItemStack part, int slot) {
        this.part = part;
        this.slot = slot;
    }
}
