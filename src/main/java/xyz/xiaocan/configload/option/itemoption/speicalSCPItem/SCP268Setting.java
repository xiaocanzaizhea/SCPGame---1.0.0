package xyz.xiaocan.configload.option.itemoption.speicalSCPItem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SCP268Setting {

    private static SCP268Setting instance;

    public String id;
    public String displayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public float invisibleTime;
    public float usageTime;
    public float cd;
    public String equipSound;
    public String useSound;

    public SCP268Setting() {
        this.id = "scp268";
        this.displayName = "scp268";
        this.material = Material.DIAMOND;
        this.customModelData = 1;
        this.lore = Arrays.asList("null");
        this.invisibleTime = 10;
        this.usageTime = 3;
        this.cd = 5;
        this.equipSound = null;
        this.useSound = null;
    }

    public static SCP268Setting getInstance(){
        if(instance==null){
            instance = new SCP268Setting();
        }
        return instance;
    }
}
