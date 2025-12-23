package xyz.xiaocan.scpitemstacks;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.scpsystems.respawnsystem.RespawnSystem;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.tools.progressBar;

import java.util.List;
import java.util.UUID;

public abstract class AbstractSCPItem{
    protected String id;
    protected String disPlayName;

    protected Material material;
    protected int customModelData;
    protected List<String> lore;
    public static final NamespacedKey key = new NamespacedKey(SCPMain.getInstance(), "scp_item_id");

    public UUID uuid;

    public AbstractSCPItem(String id, String disPlayName,
                           Material material, int customModelData, List<String> lore) {
        this.id = id;
        this.disPlayName = disPlayName;
        this.material = material;
        this.customModelData = customModelData;
        this.lore = lore;

        this.uuid = UUID.randomUUID(); //新建的对象每个都不同，因为uuid的不同

        ItemManager.getInstance().getAllScpItems().put(id + uuid,this);
    }

    public String getIdFromPersistent(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(AbstractSCPItem.key,
                PersistentDataType.STRING); //这个id用于访问实例
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData((int) customModelData);
        meta.setDisplayName(disPlayName);
        meta.getPersistentDataContainer().set(key,
                PersistentDataType.STRING,id + uuid);
        meta.setMaxStackSize(1);
        meta.setLore(lore);
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    //用来获取使用物品得到的分数
    protected void getPointFromItem(Player player, PointAndTime pointAndTime){
        RoleCategory camp = TeamManager.getInstance().getAllPlayersMapping().get(player.getUniqueId()).getEntity().getRoleTemplate().getCamp();
        if(camp==null)return;

        if(camp==RoleCategory.MTF){
            RespawnSystem.getInstance().getMtf().addPoint(pointAndTime);
        }else if(camp==RoleCategory.CHAOS){
            RespawnSystem.getInstance().getChaos().addPoint(pointAndTime);
        }
    }

    protected void cancelTask(Player player, BukkitTask task){
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        task=null;

        progressBar.clearUseProgress(player);
    }
}
