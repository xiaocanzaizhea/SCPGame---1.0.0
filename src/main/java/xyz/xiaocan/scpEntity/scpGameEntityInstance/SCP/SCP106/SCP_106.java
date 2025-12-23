package xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP106;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.SCPEntity;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049.MedicalDevice;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.armor.ArmorManager;

import java.util.ArrayList;
import java.util.List;

public class SCP_106 extends SCPEntity {
    public SCP_106(Player player, RoleTemplate roleTemplate) {
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

        ArmorManager.getInstance().createSCP106Suit(player);    //todo
        ItemStack entityItems = createEntityItems();
        this.player.getInventory().addItem(entityItems);
    }

    public ItemStack createEntityItems(){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "左键: " + ChatColor.GRAY + "攻击敌人");
        lore.add(ChatColor.GOLD + "Shift键: " + ChatColor.GRAY + "遁入地下");

        SCP106Item scp106Item = new SCP106Item("corrosionitem",
                "腐蚀器", Material.BLACK_DYE,1, lore, player);

        return scp106Item.createItemStack();
    }

    @Override
    public void update(){
        super.update();

        // 回血回盾任务创建
        if(healingTask == null){
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
                "§7你是 §c§lSCP-106",  // 大字体
                "§7与其他 §cSCP §7合作,消灭所有人类,阻止人类逃离设施",  // 小字体
                10, 100, 10
        );
    }
}
