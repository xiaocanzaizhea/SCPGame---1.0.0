package xyz.xiaocan.configload.option.itemoption;

import org.bukkit.Material;

import java.util.List;

public class RadiosSetting {
    private static RadiosSetting instance;

    public String id;
    public String displayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public float totalPower;
    public float inSRWait;
    public float inSRUsing;
    public float inMRWait;
    public float inMRUsing;
    public float inLRWait;
    public float inLRUsing;
    public float inURWait;
    public float inURUsing;
    public float eachCharacterConsumes;

    public RadiosSetting() {
        this.id = "radios";
        this.displayName = "对讲机";
        this.material = Material.IRON_INGOT;
        this.customModelData = 1;
        this.lore = null;
        this.totalPower = 200;
        this.inSRWait = 0.05f;
        this.inSRUsing = 0.33f;
        this.inMRWait = 0.2f;
        this.inMRUsing = 1f;
        this.inLRWait = 0.7f;
        this.inLRUsing = 2f;
        this.inURWait = 2.2f;
        this.inURUsing = 6f;

        this.eachCharacterConsumes = 0.1f;
    }

    public static RadiosSetting getInstance(){
        if(instance==null){
            instance = new RadiosSetting();
        }
        return instance;
    }
}
