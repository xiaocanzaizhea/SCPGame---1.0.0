package xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.xiaocan.configload.option.itemoption.RadiosSetting;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.Radios.Radio;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.medical.MedicalBag;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.weapon.gun.GunType;
import xyz.xiaocan.scpitemstacks.weapon.gun.mtfGuns.CROSSVEC;

import java.util.HashMap;
import java.util.Map;

public class MTFPrivate extends Human{
    public MTFPrivate(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
    }
    @Override
    public void spawn(){
        super.spawn();
        initSCPPlayerInventory();
        sendTitle();
    }
    @Override
    public void initSCPPlayerInventory() {
        super.initSCPPlayerInventory();

        ArmorManager.getInstance().createMTFSoldierSuit(player);
        ItemManager itemManager = ItemManager.getInstance();
        PlayerInventory inventory = player.getInventory();

        MedicalBag medicalBag = new MedicalBag(
                itemManager.getAllMedicals()
                        .get(MedicalType.MEDICALBAG));

        CROSSVEC crossvec = new CROSSVEC(itemManager.allGuns.get(GunType.CROSSVEC));

        Card card = itemManager.getAllCards().get(CardType.Private1);

        Radio radio = new Radio(RadiosSetting.getInstance());

        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.A919, 90);
        ammo.put(AmmoType.A556, 40);
        ItemManager.getInstance().allPlayersAmmo.put(player.getUniqueId(), ammo);

        inventory.addItem(
                crossvec.createItemStack(),
                medicalBag.createItemStack(),
                card.createCardItemStack(),
                radio.createItemStack()
        );
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §9§l九尾狐列兵",  // 大字体
                "§7帮助 §e科学家 §7逃离设施，消灭其他事物, 服从上级指挥",  // 小字体
                10, 100, 10
        );
    }
}
