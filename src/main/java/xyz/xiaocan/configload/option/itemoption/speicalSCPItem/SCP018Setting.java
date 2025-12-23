package xyz.xiaocan.configload.option.itemoption.speicalSCPItem;

import org.bukkit.Material;
import java.util.List;

public class SCP018Setting {
    private static SCP018Setting instance;

    public String id;
    public String disPlayName;
    public Material material;
    public int customModelData;
    public List<String> lore;

    public float moveTime;
    public float bufferTime;
    public float speedMutValue;
    public float damageMultiplierDoor;
    public float damageMultiplierSCP;
    public float baseDamage;
    public float explosionDamage;

    public static SCP018Setting getInstance(){
        if(instance == null){
            instance = new SCP018Setting();
        }
        return instance;
    }
}