package xyz.xiaocan.scpEntity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import xyz.xiaocan.chatsystem.ChatChannel;
import xyz.xiaocan.chatsystem.DistanceChatManager;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.itemoption.RadiosSetting;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.Radios.Radio;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.medical.MedicalBag;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.weapon.gun.GunType;
import xyz.xiaocan.scpitemstacks.weapon.gun.mtfGuns.FSP9;
import xyz.xiaocan.scpsystems.respawnsystem.RespawnSystem;

import java.util.HashMap;
import java.util.Map;

public class Admin extends GameEntity{
    public Admin(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);

        RespawnSystem.getInstance().addBarToPlayer(player); //对其显示重生板
        DistanceChatManager
                .setPlayerChatMode(player, ChatChannel.Admin); //加入管理员对话频道
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

        ArmorManager.getInstance().createTestRoleSuit(player);
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 " + ChatColor.LIGHT_PURPLE + "§l测试人员",  // 大字体
                 null,
                10, 100, 10
        );
    }
}
