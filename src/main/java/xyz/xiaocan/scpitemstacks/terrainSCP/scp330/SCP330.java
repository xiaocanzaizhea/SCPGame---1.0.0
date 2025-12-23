package xyz.xiaocan.scpitemstacks.terrainSCP.scp330;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.visual.StickSelectLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class SCP330 {
    public static SCP330 instance;

    public Map<UUID, Integer> playerClickNum; //存储玩家和330交互的次数，死亡会更新

    public ItemDisplay itemDisplay;
    public Interaction interaction;

    public static NamespacedKey scp330
            = new NamespacedKey(SCPMain.getInstance(), "scp330");

    public SCP330(){
        playerClickNum = new HashMap<>();
        handleDisplayCreate();
    }

    private void handleDisplayCreate() {
        Location first = StickSelectLocation.firstLocation;
        interaction = first.getWorld().spawn(first.clone().add(0.5,1,0.5),
                Interaction.class);

        itemDisplay = first.getWorld().spawn(first.clone().add(0.5,1.5,0.5),
                ItemDisplay.class);

        ItemStack itemStack = new ItemStack(Material.COAL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(330);
        itemStack.setItemMeta(meta);

        itemDisplay.setItemStack(itemStack);

        interaction.setInteractionWidth(1f);
        interaction.setInteractionHeight(0.3f);
        interaction.getPersistentDataContainer().
                set(scp330, PersistentDataType.STRING, "scp330");
    }

    public static SCP330 getInstance(){
        if(instance==null){
            instance=new SCP330();
        }
        return instance;
    }
}
