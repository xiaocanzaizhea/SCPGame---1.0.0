package xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.itemoption.RadiosSetting;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.Radios.Radio;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.medical.MedicalBag;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.scpitemstacks.medical.Stimulant;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.weapon.gun.GunType;
import xyz.xiaocan.scpitemstacks.weapon.gun.mtfGuns.CROSSVEC;
import xyz.xiaocan.scpitemstacks.weapon.gun.mtfGuns.FR_MG_0;

import java.util.HashMap;
import java.util.Map;

public class MTFCaptain extends Human {
    public MTFCaptain(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createMTFCommanderSuit(player);
        ItemManager itemManager = ItemManager.getInstance();
        PlayerInventory inventory = player.getInventory();

        Stimulant stimulant = new Stimulant(
                itemManager.getAllMedicals()
                        .get(MedicalType.STIMULANT));

        FR_MG_0 frMg0 = new FR_MG_0(
                itemManager.allGuns.get(GunType.FR_MG_0));

        //TODO 手雷

        Card card = itemManager.getAllCards().get(CardType.Commander);

        Radio radio = new Radio(RadiosSetting.getInstance());

        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.A919, 40);
        ammo.put(AmmoType.A556, 130);
        ItemManager.getInstance().allPlayersAmmo.put(player.getUniqueId(), ammo);

        inventory.addItem(
                frMg0.createItemStack(),
                stimulant.createItemStack(),
                card.createCardItemStack(),
                radio.createItemStack()
        );
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §9§l九尾狐指挥官",  // 大字体
                "§7帮助 §e科学家 §7逃离设施，消灭其他事物,指挥部下",  // 小字体
                10, 100, 10
        );
    }
}
