package xyz.xiaocan.configload.option.itemoption.gun;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.weapon.gun.GunType;

import java.util.List;

@Getter
@Setter
public class Gun {

    public GunType gunType;
    public String disPlayName;
    public double damage;
    public double reloadTime;
    public AmmoType ammoType;
    public double rateOfFire;
    public int maxAmmo;
    public int customModelData;
    public double aimingAccuracy;
    public double waistShootAccuracy;
    public String fireSound;
    public String equipSound;

    public List<String> lore;
    public Material material;

    public Gun(GunType gunType, String disPlayName,
               double damage, double reloadTime, AmmoType ammoType,
               double rateOfFire, int maxAmmo, int customModelData,
               double aimingAccuracy, double waistShootAccuracy,
               List<String> lore, Material material, String fireSound, String equipSound) {
        this.gunType = gunType;
        this.disPlayName = disPlayName;
        this.damage = damage;
        this.reloadTime = reloadTime;
        this.ammoType = ammoType;
        this.rateOfFire = rateOfFire;
        this.maxAmmo = maxAmmo;
        this.customModelData = customModelData;
        this.aimingAccuracy = aimingAccuracy;
        this.waistShootAccuracy = waistShootAccuracy;
        this.lore = lore;
        this.material = material;
        this.fireSound = fireSound;
        this.equipSound = equipSound;
    }
}
