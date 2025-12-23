package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import xyz.xiaocan.elevatorsystem.ElevatorState;

@Getter
@Setter
public class ElevatorSetting {
    private static ElevatorSetting elevatorSetting;

    public double runningTime;

    private ElevatorSetting(){
        runningTime = 5.0;
    }

    public static ElevatorSetting getElevatorSetting() {
        if(elevatorSetting==null){
            return new ElevatorSetting();
        }
        return elevatorSetting;
    }
}
