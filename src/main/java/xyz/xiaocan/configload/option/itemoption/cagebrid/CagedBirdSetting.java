package xyz.xiaocan.configload.option.itemoption.cagebrid;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class CagedBirdSetting{
    private static CagedBirdSetting instance;

    public String id;
    public String displayName;
    public Material material;
    public List<String> lore;
    public int customModelData;

    public double normalDamage;
    public double normalAttckCD;

    public double chargeDamage;
    public double chargeTime;
    public double chargeAttackSpeed;
    public double chargeAttackTime;
    public double chargeAttackDistance;

    public int MaxUseCount;



    private CagedBirdSetting(){
        normalDamage = 40;
        normalAttckCD = 5.0;

        chargeDamage = 200;
        chargeTime = 3;
        chargeAttackSpeed = 1;
        chargeAttackTime = 3;
        chargeAttackDistance = 2;

        MaxUseCount = 5;
    }

    public static CagedBirdSetting getInstance(){
        if(instance==null){
            instance=new CagedBirdSetting();
        }
        return instance;
    }
}
