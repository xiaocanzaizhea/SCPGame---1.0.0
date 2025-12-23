package xyz.xiaocan.configload.option.itemoption.speicalSCPItem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SCP207Setting {
    private static SCP207Setting instance;

    public String id;
    public String disPlayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public float speedUpValue;
    public float useTime;
    public float subValue;
    public String equipSound;
    public String useSound;

    public SCP207Setting() {
        this.id = "scp207";
        this.disPlayName = "scp207";
        this.material = Material.POTION;
        this.customModelData = 1;
        this.lore = Arrays.asList("null");
        this.speedUpValue = 0.15f;
        this.useTime = 3;
        this.subValue = 0.05f;
        this.equipSound = null;
        this.useSound = null;
    }

    public static SCP207Setting getInstance(){
        if(instance==null){
            instance = new SCP207Setting();
        }
        return instance;
    }
}
