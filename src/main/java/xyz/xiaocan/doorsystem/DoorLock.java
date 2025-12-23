package xyz.xiaocan.doorsystem;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public enum DoorLock {

    NO_LEVEL_LOCK_LIGHT_ON(40,Arrays.asList(53,52,51,50),41,0.3),
    NO_LEVEL_LOCK_LIGHT_OFF(41, Arrays.asList(50,51,52,53),40,0.3),
    LEVEL_LOCK_ONE_BLUE(10, Arrays.asList(20,30), 10,0.3),
    LEVEL_LOCK_TWO_BLUE(11, Arrays.asList(21,30), 11,0.3),
    LEVEL_LOCK_THREE_BLUE(12, Arrays.asList(22,30), 12,0.3),
    ELEVATOR_LOCK(60,Arrays.asList(61),60,0.3),
    HEAVY_LOCK_BLUE(72,Arrays.asList(71,73),70,0.6),
    HEAVY_LOCK_GREEN(70,Arrays.asList(71,73),72,0.6);
    public int id;
    public List<Integer> list;
    public int endNum;
    public double size;
    DoorLock(int id, List<Integer> list, int endNum, double size){
        this.id = id;
        this.list = list;
        this.endNum = endNum;
        this.size = size;
    }

    public int getAnimItemId(int current){
        int size = list.size();
        int num = current % size;
        Integer i = list.get(num);

        return i;
    }
}
