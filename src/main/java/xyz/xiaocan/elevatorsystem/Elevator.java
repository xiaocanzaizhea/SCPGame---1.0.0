package xyz.xiaocan.elevatorsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import xyz.xiaocan.configload.option.ElevatorSetting;
import xyz.xiaocan.doorsystem.FloorContainmentDoor;
import xyz.xiaocan.scpgame.SCPMain;

import java.util.List;

@Getter
@Setter
public class Elevator {
    public String id;
    public BoundingBox box;
    public Elevator otherElevator;  //另一个链接的电梯
    public FloorContainmentDoor door;
    public ElevatorState state;
    public ElevatorSetting elevatorSetting;

    public static NamespacedKey elevator_id =
            new NamespacedKey(SCPMain.getInstance(), "elevator_id");

    public Elevator(BoundingBox box, FloorContainmentDoor door) {
        this.box = box;
        this.door = door;

        this.id = door.getId();
        this.elevatorSetting = ElevatorSetting.getElevatorSetting();
        handleInteraction(door);
    }

    private void handleInteraction(FloorContainmentDoor door) {
        List<Interaction> interactions = door.getInteractions();
        interactions.forEach(interaction -> {
            interaction.getPersistentDataContainer().remove(FloorContainmentDoor.door_id); //移除原有的
            interaction.getPersistentDataContainer().set(elevator_id, PersistentDataType.STRING,id); //添加独有的
        });
    }

    public void setLinkElevator(Elevator elevator){
        this.otherElevator = elevator;
    }

    public void setDoorVisual(){
        door.getToggleButtons().forEach(button->{
            if(state==ElevatorState.RUNNING){
                setItemDisplay(button,61); //设置为橙色
                Bukkit.getLogger().warning("设置按钮为橙色");
            }else{
                setItemDisplay(button, 60);
            }
        });
    }

    public void setItemDisplay(ItemDisplay dispaly, int custommodeldata){
        ItemStack itemStack = new ItemStack(Material.COAL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(custommodeldata);
        itemStack.setItemMeta(meta);

        dispaly.setItemStack(itemStack);
        dispaly.setPersistent(false);
        dispaly.setBrightness(new Display.Brightness(15,15));
        dispaly.setTeleportDuration(5);
    }
}
