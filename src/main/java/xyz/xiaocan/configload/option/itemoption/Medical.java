package xyz.xiaocan.configload.option.itemoption;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class Medical {
    public String id;
    public String displayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public double usageTime;
    public double healHp;
    public double healShield;
    public double duringTime;

    public Medical(String id, String displayName,
                   Material material, int customModelData,
                   List<String> lore, double usageTime, double healHp,
                   double healShield, double duringTime) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.customModelData = customModelData;
        this.lore = lore;
        this.usageTime = usageTime;
        this.healHp = healHp;
        this.healShield = healShield;
        this.duringTime = duringTime;
    }

    @Override
    public String toString() {
        return "Medical{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", material=" + material +
                ", customModelData=" + customModelData +
                ", lore=" + lore +
                ", usageTime=" + usageTime +
                ", healHp=" + healHp +
                ", healShield=" + healShield +
                ", duringTime=" + duringTime +
                '}';
    }
}
