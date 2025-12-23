package xyz.xiaocan.scpitemstacks.medical;

import org.bukkit.Material;
import xyz.xiaocan.configload.option.itemoption.Medical;
import xyz.xiaocan.scpitemstacks.medical.absClass.MedicalSCPItem;

import java.util.List;

public class Stimulant extends MedicalSCPItem {
    public Stimulant(Medical medical) {
        super(medical.id, medical.displayName, medical.usageTime,
                medical.duringTime, medical.healHp, medical.healShield,
                medical.material, medical.customModelData, medical.lore);
    }
}
