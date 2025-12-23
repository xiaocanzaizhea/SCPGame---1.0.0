package xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.Human;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;

public class DCLASS extends Human{
    public DCLASS(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createDClassSuit(player);
        PlayerInventory inventory = player.getInventory();

        ItemManager itemManager = ItemManager.getInstance();
        Card card = itemManager.getAllCards().get(CardType.Janitor);
        inventory.addItem(card.createCardItemStack());
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §6§lD级人员",  // 大字体
                "§7与 §a混沌分裂者 §7合作逃离设施，尽量避开其他阵营",  // 小字体
                10, 100, 10
        );
    }

    @Override
    public PointAndTime getKillPoint(){
        return Target.getInstance().getKillUnarmedPeople();
    }
}
