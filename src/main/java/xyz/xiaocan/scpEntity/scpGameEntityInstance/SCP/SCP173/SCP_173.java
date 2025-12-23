package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP173;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.scpoption.SCP173SpiecalSetting;
import xyz.xiaocan.scpEntity.GameEntity;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.tools.util;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SCP_173 extends SCPEntity{
    //F键，超高速，左键掐人，shift产生泥巴，右键按住瞬移
    //超高速不可以掐人
    //被看有cd，过了cd就可以瞬移掐人
    public SCP_173(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createSCP173Suit(player);
        ItemStack mushroom = createMushroom();
        this.player.getInventory().addItem(mushroom);
    }

    public ItemStack createMushroom(){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "F键: " + ChatColor.GRAY + "持续一段时间的超高速");
        lore.add(ChatColor.GOLD + "左键: " + ChatColor.GRAY + "在敌人背后拧断脖子");
        lore.add(ChatColor.GOLD + "Shift键: " + ChatColor.GRAY + "生成一摊烂泥");
        lore.add(ChatColor.GOLD + "右键: " + ChatColor.GRAY + "被注释一段时间后瞬移击杀敌人");

        Mushroom mushroom = new Mushroom("mushroom","mushroom",
                Material.BROWN_MUSHROOM,1, lore, player);

        return mushroom.createItemStack();
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
                "§7你是 §c§lSCP-173",  // 大字体
                "§7与其他 §cSCP §7合作,消灭所有人类,阻止人类逃离设施",  // 小字体
                10, 100, 10
        );
    }
}
