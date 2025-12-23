package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.scpoption.SCP049SpiecalSetting;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.tools.util;

import java.util.*;

@Getter
@Setter
public class SCP_049 extends SCPEntity {
    public SCP_049(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createSCP049Suit(player);
        ItemStack entityItems = createEntityItems();
        this.player.getInventory().addItem(entityItems);
    }

    public ItemStack createEntityItems(){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "左键: " + ChatColor.GRAY + "治疗敌人");
        lore.add(ChatColor.GOLD + "右键: " + ChatColor.GRAY + "为周围049-2回复护盾 (暂时在写)");
        lore.add(ChatColor.GOLD + "F键: " + ChatColor.GRAY + "标记敌人");
        lore.add(ChatColor.GOLD + "Shift键: " + ChatColor.GRAY + "复活死亡的敌人 (暂时在写)");

        MedicalDevice medicalDevice = new MedicalDevice("medicalDevice",
                "医疗器",Material.RED_DYE,1,lore, player);

        return medicalDevice.createItemStack();
    }

    @Override
    public void update(){
        super.update();

        // 回血回盾任务创建
        if(healingTask == null){  //1、移动取消回血 2、要站立不动一段时间才可以回血
            healingTask = new BukkitRunnable(){

                @Override
                public void run() {
                    if(!player.isOnline() || player.isDead()){
                        this.cancel();
                    }

                    healShield();
                }
            }.runTaskTimer(SCPMain.getInstance(),0l,20l);
        }
    }

    @Override
    protected void sendTitle(){
        player.sendTitle(
                "§7你是 §c§lSCP-049",  // 大字体
                "§7与其他 §cSCP §7合作,消灭所有人类,阻止人类逃离设施",  // 小字体
                10, 100, 10
        );
    }
}
