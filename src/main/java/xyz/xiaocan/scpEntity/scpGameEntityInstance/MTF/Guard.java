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
import xyz.xiaocan.scpitemstacks.weapon.gun.mtfGuns.FSP9;

import java.util.HashMap;
import java.util.Map;

public class Guard extends Human {
    public Guard(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createGuardSuit(player);
        ItemManager itemManager = ItemManager.getInstance();
        PlayerInventory inventory = player.getInventory();

        FSP9 fsp9 = new FSP9(itemManager.allGuns.get(GunType.FSP9));

        MedicalBag medicalBag = new MedicalBag(
                itemManager.getAllMedicals()
                        .get(MedicalType.MEDICALBAG));

        Radio radio = new Radio(RadiosSetting.getInstance());

        Card card = itemManager.getAllCards().get(CardType.Guard);

        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.A919, 80);
        ItemManager.getInstance().allPlayersAmmo.put(player.getUniqueId(), ammo);

        inventory.addItem(
                fsp9.createItemStack(),
                medicalBag.createItemStack(),
                radio.createItemStack(),
                card.createCardItemStack()
        );
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §b§l警卫",  // 大字体
                "§7与 §9MTF §7合作,帮助 §e科学家 §7逃离设施，消灭其他事物",  // 小字体
                10, 100, 10
        );
    }
}
