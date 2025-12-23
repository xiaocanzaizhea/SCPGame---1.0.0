package xyz.xiaocan.scpitemstacks.weapon.grenade;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;

import java.util.List;

@Getter
@Setter
public abstract class AbstractGrenade extends AbstractSCPItem {
    protected double radius;
    protected double explosionTime;

    public AbstractGrenade(String id, String disPlayName,
                           Material material, int customModelData,
                           List<String> lore, double radius, double explosionTime) {
        super(id, disPlayName, material, customModelData, lore);
        this.radius = radius;
        this.explosionTime = explosionTime;
    }
}