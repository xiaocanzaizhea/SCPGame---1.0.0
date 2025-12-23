package xyz.xiaocan.elevatorsystem;

import java.util.HashMap;
import java.util.Map;

public class ElevatorManager {
    private static ElevatorManager instance;
    public Map<String, Elevator> ElevatorInstances;

    private ElevatorManager(){
        ElevatorInstances = new HashMap<>();
    }

    public static ElevatorManager getInstance(){
        if(instance==null)instance = new ElevatorManager();
        return instance;
    }
}
