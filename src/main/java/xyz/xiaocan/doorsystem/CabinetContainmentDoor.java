package xyz.xiaocan.doorsystem;

import org.bukkit.Location;
import xyz.xiaocan.configload.option.DoorTemplate;

public class CabinetContainmentDoor extends ContainmentDoor {

    public CabinetContainmentDoor(String id, Location origin, Location second,
                                  int width, int height, int depth,
                                  double moveDistance, DoorTemplate doorTemplate) {
        super(id, origin, second, width,  height, depth, moveDistance, doorTemplate);
    }
}
