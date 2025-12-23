package xyz.xiaocan.configload.option.itemoption.speicalSCPItem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class SCP1344Setting {
    private static SCP1344Setting instance;

    public String id;
    public String displayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public float usageTime;
    public String equipSound;
    public String useSound;

    public SCP1344Setting() {
        this.id = "scp1344";
        this.displayName = "scp1344";
        this.material = Material.NAUTILUS_SHELL;
        this.customModelData = 1;
        this.lore = null;
        this.usageTime = 5f;
        this.equipSound = null;
        this.useSound = null;
    }

    public static SCP1344Setting getInstance(){
        if(instance==null){
            instance = new SCP1344Setting();
        }
        return instance;
    }
}
