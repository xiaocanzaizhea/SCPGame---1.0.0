package xyz.xiaocan.configload.option.scpoption;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public class SCP0492SpecialSetting {
    private static SCP0492SpecialSetting instance;
    private String id;
    private float damage;
    private float cd;

    private float eatHealHpCnt;

    private int maxSpeedAddLayers;
    private float speedAddCnt;
    private float speedSubCnt;

    public SCP0492SpecialSetting() {
        this.id = "scp0492";
        this.damage = 40;
        this.cd = 1.5f;
        this.eatHealHpCnt = 150;
        this.maxSpeedAddLayers = 10;
        this.speedAddCnt = 0.02f;
        this.speedSubCnt = 0.02f;
    }

    public static SCP0492SpecialSetting getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("SCP0492SpecialSetting实例为空,已创建一个新实例");
            return new SCP0492SpecialSetting();
        }
        return instance;
    }
}
