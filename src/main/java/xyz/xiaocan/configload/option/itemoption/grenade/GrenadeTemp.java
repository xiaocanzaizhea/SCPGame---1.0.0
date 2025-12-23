package xyz.xiaocan.configload.option.itemoption.grenade;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.List;

@Getter
public class GrenadeTemp {
    private static GrenadeTemp instance;

    private String id;
    private String displayName;
    private Material material;
    private int customModelData;
    private List<String> lore;

    private double radius;
    private double explosionTime;
    private double damage;

    public GrenadeTemp(String id, String displayName, String material,
                       int customModelData, List<String> lore, double radius,
                       double explosionTime, double damage) {
        this.id = id;
        this.displayName = displayName;
        this.material = Material.valueOf(material);
        this.customModelData = customModelData;
        this.lore = lore;
        this.radius = radius;
        this.explosionTime = explosionTime;
        this.damage = damage;
    }

    public static GrenadeTemp getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("手雷文件为空");
            return null;
        }
        return instance;
    }

    public static void setInstance(GrenadeTemp grenadeTemp){
        instance = grenadeTemp;
    }
}
