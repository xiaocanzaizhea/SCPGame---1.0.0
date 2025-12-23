package xyz.xiaocan.configload.option.itemoption.gun;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class Ammo{
    public String id;
    public String displayName;
    public Material material;
    public int customModelData;
    public List<String> lore;
    public int maxAmmoTake;

    public Ammo(String id, String displayName,
                int maxAmmoTake, double maxDistance) {
        this.id = id;
        this.displayName = displayName;
        this.maxAmmoTake = maxAmmoTake;

    }
}
