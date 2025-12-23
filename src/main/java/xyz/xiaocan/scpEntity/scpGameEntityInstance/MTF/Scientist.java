package xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.medical.MedicalBag;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;

public class Scientist extends Human{
    public Scientist(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createScientistSuit(player);
        PlayerInventory inventory = player.getInventory();

        ItemManager itemManager = ItemManager.getInstance();
        Card card = itemManager.getAllCards().get(CardType.Scientist);

        MedicalBag medicalBag = new MedicalBag(
                itemManager.getAllMedicals()
                        .get(MedicalType.MEDICALBAG));

        inventory.addItem(
                card.createCardItemStack(),
                medicalBag.createItemStack()
                );
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §f§l科学家",  // 大字体
                "§7与 §9MTF §7合作,§7逃离设施，尽量避开其他阵营",  // 小字体
                10, 100, 10
        );
    }

    @Override
    public PointAndTime getKillPoint(){
        return Target.getInstance().getKillUnarmedPeople();
    }
}
