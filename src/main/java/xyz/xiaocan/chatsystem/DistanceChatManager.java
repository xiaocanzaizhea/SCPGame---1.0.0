package xyz.xiaocan.chatsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.xiaocan.scpitemstacks.Radios.RadiosStates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DistanceChatManager {
    public static final Map<UUID, ChatChannel> playerModes = new HashMap<>();
    public static final double localChatRange = 20;
    public static final double sr = RadiosStates.SR.maxDistance;
    public static final double mr = RadiosStates.MR.maxDistance;
    public static final double lr = RadiosStates.LR.maxDistance;
    public static final double ur = RadiosStates.UR.maxDistance;

    public static void initPlayerModes(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            playerModes.put(player.getUniqueId(),ChatChannel.LOCAL);
        });
    }

    public static ChatChannel getPlayerChatChannel(Player player){
        UUID uniqueId = player.getUniqueId();
        return playerModes.get(uniqueId);
    }

    public static void setPlayerChatMode(Player player, ChatChannel chatMode){
        playerModes.put(player.getUniqueId(), chatMode);
    }



    //------------------------------------------check方法-----------------------------------------
    public static boolean isObserver(Player player){
        return playerModes.get(player.getUniqueId())==ChatChannel.OBSERVER;
    }

    public static boolean isSCP(Player player){
        return playerModes.get(player.getUniqueId())==ChatChannel.SCP;
    }

    public static boolean isAdmin(Player player){
        return playerModes.get(player.getUniqueId())==ChatChannel.Admin;
    }
}
