package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049.scp0492;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.configload.option.scpoption.SCP0492SpecialSetting;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP173.Mushroom;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;

import java.util.ArrayList;
import java.util.List;

public class SCP_0492 extends SCPEntity {

    public SCP_0492(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
    }

    public void spawn(){
        super.spawn();
        initSCPPlayerInventory();
    }

    public void initSCPPlayerInventory() {
        super.initSCPPlayerInventory();

        ArmorManager.getInstance().createSCP0492Suit(player);
        ItemStack zombie = createZombie();
        this.player.getInventory().addItem(zombie);
    }

    public ItemStack createZombie(){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "左键: " + ChatColor.GRAY + "攻击");

        Zombie zombie = new Zombie("zombie","zombie",
                Material.BLACK_DYE,1, lore, player);

        return zombie.createItemStack();
    }

    @Override
    public PointAndTime getKillPoint(){
        return Target.getInstance().getKillSCP0492();
    }
}
