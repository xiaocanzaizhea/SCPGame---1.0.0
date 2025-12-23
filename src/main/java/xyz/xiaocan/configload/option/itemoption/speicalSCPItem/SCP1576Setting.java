package xyz.xiaocan.configload.option.itemoption.speicalSCPItem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class SCP1576Setting {
    private static SCP1576Setting instance;

    public String id;
    public String displayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public float usageTime;
    public float talkTime;
    public float cd;
    public String equipSound;
    public String useSound;

    public SCP1576Setting() {
        this.id = "scp1576";
        this.displayName = "死人电话";
        this.material = Material.NETHERITE_INGOT;
        this.customModelData = 1;
        this.lore = null;
        this.usageTime = 5;
        this.talkTime = 20;
        this.cd = 120;
        this.equipSound = null;
        this.useSound = null;
    }

    public static SCP1576Setting getInstance(){
        if(instance==null){
            instance = new SCP1576Setting();
        }
        return instance;
    }
}
