package xyz.xiaocan.scpitemstacks.speicalSCPItem.SCP127;

import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import xyz.xiaocan.configload.option.itemoption.gun.Gun;
import xyz.xiaocan.scpitemstacks.weapon.gun.absClass.GunSCPItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SCP127 extends GunSCPItem {

    private int stateNum;
    private Map<UUID, Integer> allPlayerSCP127Level;

    public SCP127(Gun gun) {
        super(gun);

        stateNum = 0;
        allPlayerSCP127Level = new HashMap<>();
    }

    @Override //127不允许换弹
    public void onSwapHandClick(PlayerSwapHandItemsEvent event) {}

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {
        super.OnLeftClick(event);
    }

    @Override
    public void onPlayerDrop(PlayerDropItemEvent event) {

    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){ //只对声音进行处理
            event.getPlayer().getWorld().playSound(
                    event.getPlayer().getLocation(),
                    String.valueOf(NamespacedKey.fromString(equipSound)),
                    SoundCategory.PLAYERS,
                    1.5f,
                    1.2f
            );
        }
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        SCP127State state = SCP127State.values()[stateNum];


    }
}
