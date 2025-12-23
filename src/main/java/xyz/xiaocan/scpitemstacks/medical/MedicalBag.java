package xyz.xiaocan.scpitemstacks.medical;

import org.bukkit.Material;
import xyz.xiaocan.configload.option.itemoption.Medical;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpitemstacks.medical.absClass.MedicalSCPItem;
import java.util.List;

public class MedicalBag extends MedicalSCPItem{
    public MedicalBag(Medical medical) {
        super(medical.id, medical.displayName, medical.usageTime,
                medical.duringTime, medical.healHp, medical.healShield,
                medical.material, medical.customModelData, medical.lore);
    }
}
