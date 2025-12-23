package xyz.xiaocan.scpitemstacks;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.configload.option.itemoption.Medical;
import xyz.xiaocan.configload.option.itemoption.grenade.GrenadeTemp;
import xyz.xiaocan.configload.option.itemoption.gun.Ammo;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.configload.option.itemoption.gun.Gun;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;
import xyz.xiaocan.scpitemstacks.weapon.grenade.GrenadeType;
import xyz.xiaocan.scpitemstacks.weapon.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.weapon.gun.GunType;
import xyz.xiaocan.scpitemstacks.weapon.gun.absClass.GunSCPItem;
import xyz.xiaocan.tools.util;

import java.util.*;

@Getter
@Setter
public class ItemManager {
    private static ItemManager instance;
    private ItemManager(){}

    //-----------------------以下是模版---------------------------
    public Map<CardType, Card> allCards = new HashMap<>();
    public Map<MedicalType, Medical> allMedicals = new HashMap<>();
    public Map<AmmoType, Ammo> allAmmos = new HashMap<>();
    public Map<GunType, Gun> allGuns = new HashMap<>();
    public Map<GrenadeType, GrenadeTemp> allGrenade = new HashMap<>();

    //-----------------------以下存储实例----------------------------
    public Map<String, AbstractSCPItem> allScpItems = new HashMap<>();
    public Map<UUID, Map<AmmoType, Integer>> allPlayersAmmo = new HashMap<>();

    public void updatePlayerInventoryAmmo(Player player){
        UUID playerId = player.getUniqueId();

        if (!allPlayersAmmo.containsKey(playerId)) {
            initPlayerAmmoData(player);
            return;
        }

        Map<AmmoType, Integer> playerAmmo = allPlayersAmmo.get(playerId);

        for (int i = 11; i <= 15; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            AmmoType ammoType = AmmoType.getBySlot(i);
            if (ammoType == null) continue;

            int totalAmmo = playerAmmo.getOrDefault(ammoType, 0);

            ItemStack bg = util.background;

            if (totalAmmo <= 0) {
                player.getInventory().setItem(i, bg);
            } else {
                item.setType(Material.STONE_BUTTON);
                ItemMeta meta = item.getItemMeta();
                meta.setLore(Arrays.asList(String.valueOf(totalAmmo)));
                item.setItemMeta(meta);
            }
        }
    }

    private void initPlayerAmmoData(Player player) {
        UUID playerId = player.getUniqueId();
        Map<AmmoType, Integer> defaultAmmo = new HashMap<>();

        for (AmmoType ammoType : AmmoType.values()) {
            defaultAmmo.put(ammoType, 0);
        }

        allPlayersAmmo.put(playerId, defaultAmmo);

        for (int i = 11; i <= 15; i++) {
            player.getInventory().setItem(i, util.background);
        }
    }

    public static ItemManager getInstance(){
        if(instance==null)instance = new ItemManager();
        return instance;
    }

    public void test(){
        Bukkit.getLogger().warning(String.valueOf(allMedicals.size()));
        Collection<Medical> values = allMedicals.values();
        for (Medical medical:values) {
            Bukkit.getLogger().warning(medical.toString());
        }
    }

}
