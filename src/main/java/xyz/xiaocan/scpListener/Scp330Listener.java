package xyz.xiaocan.scpListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.xiaocan.elevatorsystem.Elevator;
import xyz.xiaocan.elevatorsystem.ElevatorManager;
import xyz.xiaocan.scpitemstacks.terrainSCP.scp330.Candy;
import xyz.xiaocan.scpitemstacks.terrainSCP.scp330.SCP330;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Scp330Listener implements Listener {
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){

        if (!(event.getRightClicked() instanceof Interaction)) {
            return;
        }

        Interaction interaction = (Interaction) event.getRightClicked();
        String id = interaction.getPersistentDataContainer()
                .get(SCP330.scp330, PersistentDataType.STRING);

        if(id==null){
            return;
        }

        Map<UUID, Integer> playerClickNum = SCP330.getInstance().getPlayerClickNum();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Integer orDefault = playerClickNum.getOrDefault(uuid, 0);
        if(orDefault<2){
            //不断手
            Bukkit.getLogger().warning("玩家成功点击了一次糖果盒");
            //随机给玩家一颗糖果
            Candy[] values = Candy.values();
            Random random = new Random();
            int i = random.nextInt(values.length);
            Candy candy = values[i];

            playerClickNum.put(uuid, orDefault + 1);
            player.getInventory().addItem(candy.itemStack);

        }else{//处理断手

        }


    }
}
