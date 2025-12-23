package xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.card.Decoder;
import xyz.xiaocan.scpitemstacks.medical.MedicalBag;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.scpitemstacks.medical.Painkiller;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.weapon.gun.GunType;
import xyz.xiaocan.scpitemstacks.weapon.gun.chaosGuns.AK;

import java.util.HashMap;
import java.util.Map;

public class ChaosConscript extends Human {
    public ChaosConscript(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createChaosSuit(player);
        PlayerInventory inventory = player.getInventory();

        ItemManager itemManager = ItemManager.getInstance();
        Decoder decoder = new Decoder(itemManager.allCards.get(CardType.Decoder));

        AK ak = new AK(itemManager.allGuns.get(GunType.AK));

        Painkiller painkiller = new Painkiller(
                itemManager.getAllMedicals().get(MedicalType.PAINKILLER));
        MedicalBag medicalBag = new MedicalBag(
                itemManager.getAllMedicals().get(MedicalType.MEDICALBAG));

        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.A762, 80);
        ammo.put(AmmoType.A12, 7);
        ItemManager.getInstance().allPlayersAmmo.put(player.getUniqueId(), ammo);

        inventory.addItem(ak.createItemStack(),
                painkiller.createItemStack(),
                medicalBag.createItemStack(),
                decoder.createItemStack()
        );
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §a§l混沌分裂者 征召兵",  // 大字体
                "§7帮助 §6D级人员 §7逃离设施，消灭其他事物",  // 小字体
                10, 100, 10
        );
    }
}
