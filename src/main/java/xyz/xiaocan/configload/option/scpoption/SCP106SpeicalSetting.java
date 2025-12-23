package xyz.xiaocan.configload.option.scpoption;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;


@Setter
@Getter
public class SCP106SpeicalSetting {
    private static SCP106SpeicalSetting instance;

    private String id;

    private float attackCD;
    private float skillCD;
    private float moveSpeedDuringSkill;

    private float skillDuration;

    private Location pocketSpaceLocation =
            new Location(Bukkit.getWorlds().getFirst(), 1,1,1);

    public SCP106SpeicalSetting(String id, float attackCD,
                                float skillCD,
                                float moveSpeedDuringSkill) {
        this.id = id;
        this.attackCD = attackCD;
        this.skillCD = skillCD;
        this.moveSpeedDuringSkill = moveSpeedDuringSkill;

        this.skillDuration = 3;

        instance = this;
    }

    public static SCP106SpeicalSetting getInstance(){
        if(instance==null){
            return instance = new SCP106SpeicalSetting(
                    "106",
                    1.2f,
                    2.0f,
                    0.3f
            );
        }
        return instance;
    }
}
