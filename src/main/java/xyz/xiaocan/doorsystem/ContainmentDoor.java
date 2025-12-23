package xyz.xiaocan.doorsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.DoorTemplate;
import xyz.xiaocan.scpgame.SCPMain;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContainmentDoor {
    protected String id;
    protected Location first;
    protected Location second;
    protected int width, height, depth;

    protected Vector moveDirection;
    protected double moveDistance;

    //debugmode显示
    protected TextDisplay textDisplay;
    protected BukkitTask task;

    protected DoorState state;
    protected DoorTemplate doorTemplate;

    protected BoundingBox collisionBox;

    protected List<Block> doorBoundBlocks;   //门包含的方块 (屏障或者玻璃板) ,控制他的出现和消失
    protected List<BlockDisplay> movingDisplays = new ArrayList<>();  //常驻

    protected List<ItemDisplay> icons;
    protected List<Location> iconsOriginLoc;

    protected List<ItemDisplay> toggleButtons;
    protected List<Interaction> interactions;

    public static NamespacedKey door_id =
            new NamespacedKey(SCPMain.getInstance(), "door_id");


    public ContainmentDoor(String id, Location first, Location second,
                           int width, int height, int depth,
                           double moveDistance,
                           DoorTemplate doorTemplate) {
        this.id = id;
        this.first = first;
        this.second = second;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.moveDistance = moveDistance;
        this.doorTemplate = doorTemplate;
        this.state = DoorState.CLOSED;

        this.doorBoundBlocks = new ArrayList<>();

        this.icons = new ArrayList<>();
        this.iconsOriginLoc = new ArrayList<>();

        this.toggleButtons = new ArrayList<>();
        this.interactions = new ArrayList<>();
    }
}
