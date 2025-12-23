package xyz.xiaocan.configload.option.scpoption;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public class SCP173SpiecalSetting {
    private static SCP173SpiecalSetting instance;

    private String id;
    private float damage;
    private float radius;

    private float cdOfTeleport;
    private float teleportDistance;

    private float cdOfMud;
    private float mudDuringTime;

    private float cdOfHighSpeed;
    private float highSpeedDuringTime;
    private float highSpeedAdd;
    private float highSpeedDistanceAdd;
    private float percentOfTeleportTime;

    public SCP173SpiecalSetting(String id,
                                float damage, float radius,
                                float cdOfTeleport, float teleportDistance,
                                float cdOfMud, float mudDuringTime,
                                float cdOfHighSpeed, float highSpeedDuringTime,
                                float highSpeedAdd, float highSpeedDistanceAdd, float percentOfTeleportTime) {
        this.id = id;
        this.damage = damage;
        this.radius = radius;
        this.cdOfTeleport = cdOfTeleport;
        this.teleportDistance = teleportDistance;
        this.cdOfMud = cdOfMud;
        this.mudDuringTime = mudDuringTime;
        this.cdOfHighSpeed = cdOfHighSpeed;
        this.highSpeedDuringTime = highSpeedDuringTime;
        this.highSpeedAdd = highSpeedAdd;
        this.highSpeedDistanceAdd = highSpeedDistanceAdd;
        this.percentOfTeleportTime = percentOfTeleportTime;

        instance = this;
    }

    @Override
    public String toString() {
        return "SCP173SpiecalSetting{" +
                "id='" + id + '\'' +
                ", damage=" + damage +
                ", radius=" + radius +
                ", cdOfTeleport=" + cdOfTeleport +
                ", teleportDistance=" + teleportDistance +
                ", cdOfMud=" + cdOfMud +
                ", mudDuringTime=" + mudDuringTime +
                ", cdOfHighSpeed=" + cdOfHighSpeed +
                ", highSpeedDuringTime=" + highSpeedDuringTime +
                ", highSpeedAdd=" + highSpeedAdd +
                ", highSpeedDistanceAdd=" + highSpeedDistanceAdd +
                ", percentOfTeleportTime=" + percentOfTeleportTime +
                '}';
    }

    public static SCP173SpiecalSetting getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("SCP173SpiecalSetting实例为空");
            return null;
        }
        return instance;
    }
}
