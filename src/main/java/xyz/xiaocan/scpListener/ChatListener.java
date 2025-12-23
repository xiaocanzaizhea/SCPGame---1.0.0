package xyz.xiaocan.scpListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;
import xyz.xiaocan.chatsystem.ChatChannel;
import xyz.xiaocan.chatsystem.DistanceChatManager;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.Radios.Radio;
import xyz.xiaocan.scpitemstacks.Radios.RadiosStates;

import static xyz.xiaocan.chatsystem.DistanceChatManager.*;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        event.setCancelled(true);

        ChatChannel channel = DistanceChatManager.getPlayerChatChannel(event.getPlayer());

        if(channel==ChatChannel.OBSERVER){
            sendObserverMessage(sender, message);
            return;
        }

        switch (channel) {
            case GLOBAL:
                sendGlobalMessage(sender, message);
                break;
            case Admin:
                sendAdminMessage(sender, message);
                break;
            case LOCAL:
                sendLocalMessage(sender, message, DistanceChatManager.localChatRange);
                break;
            case RADIO_SR:
                sendRadioMessage(sender, message, sr);
                break;
            case RADIO_MR:
                sendRadioMessage(sender, message, DistanceChatManager.mr);
                break;
            case RADIO_LR:
                sendRadioMessage(sender, message, DistanceChatManager.lr);
                break;
            case RADIO_UR:
                sendRadioMessage(sender, message, DistanceChatManager.ur);
                break;
            case SCP:
                sendSCPMessage(sender, message);
                break;
        }
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event){
        Player player = event.getEntity();
        DistanceChatManager.setPlayerChatMode(player, ChatChannel.OBSERVER);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DistanceChatManager.setPlayerChatMode(player,ChatChannel.LOCAL);
    }

    private void sendGlobalMessage(Player sender, String message) {
        String formatted = formatMessage(sender, ChatChannel.GLOBAL, message);
        Bukkit.broadcastMessage(formatted);
    }

    private void sendAdminMessage(Player sender, String message) {
        String formatted = formatMessage(sender, ChatChannel.Admin, message);
        Bukkit.broadcastMessage(formatted);
    }

    private void sendLocalMessage(Player sender, String message, double range) {
        String formatted = formatMessage(sender, ChatChannel.LOCAL, message);
        sendRangeMessage(sender, formatted, range);
    }

    private void sendRadioMessage(Player sender, String message, double range) {
        String formatted = formatMessage(sender, getRadioChannel(range), message);
        sendRangeMessageWithRaido(sender, formatted, message.length());
    }

    private void sendSCPMessage(Player sender, String message) {
        if (!isSCP(sender)) {
            sender.sendMessage(ChatColor.RED + "只有SCP可以使用SCP频道！");
            return;
        }

        String formatted = formatMessage(sender, ChatChannel.SCP, message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isSCP(player) || isAdmin(player)) {
                player.sendMessage(formatted);
            }
        }
    }

    private void sendObserverMessage(Player sender, String message) {
        if (!isObserver(sender)) {
            sender.sendMessage(ChatColor.RED + "只有观察者可以使用观察者频道！");
            return;
        }

        String formatted = formatMessage(sender, ChatChannel.OBSERVER, message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isObserver(player) || isAdmin(player)) {
                player.sendMessage(formatted);
            }
        }
    }

    private void sendRangeMessage(Player sender, String formattedMessage, double range) {
        Location senderLoc = sender.getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {  //观察者可见， 并且玩家距离小于一定距离可见，管理员可见
            if (isObserver(player) ||
                    player.getLocation().distance(senderLoc) <= range ||
                            isAdmin(player)) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    private void sendRangeMessageWithRaido(Player sender, String s,int l) {
        Location senderLoc = sender.getLocation();
        Radio radio = getRadio(sender);
        if(!canHandleMesssage(radio))return;

        float range = RadiosStates.values()[radio.stateNum].maxDistance;
        radio.consumePower(sender, true, l);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Radio radio1 = getRadio(player);
            if(!canHandleMesssage(radio1))continue;

            float range1 = RadiosStates.values()[radio1.stateNum].maxDistance;
            float dis = Math.max(range, range1);  //取两者最大值,来决定是否要发送
            if (isObserver(player) ||
                    player.getLocation().distance(senderLoc) <= dis ||
                        isAdmin(player)) {


                radio1.consumePower(player,false, l);
                player.sendMessage(s);
            }

        }
    }

    public boolean canHandleMesssage(Radio radio){
        return radio!=null &&
                radio.isTurnOn &&
                radio.currentPower>0;
    }

    private Radio getRadio(Player player){
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            String s = item.getItemMeta().getPersistentDataContainer()
                    .get(Radio.key, PersistentDataType.STRING);
            if(s!=null){
                AbstractSCPItem abstractSCPItem = ItemManager.getInstance().allScpItems.get(s);
                Radio radio = (Radio) abstractSCPItem;
                return radio;
            }
        }
        return null;
    }

    private ChatChannel getRadioChannel(double range) {
        if (range <= sr) return ChatChannel.RADIO_SR;
        if (range <= mr) return ChatChannel.RADIO_MR;
        if (range <= lr) return ChatChannel.RADIO_LR;
        return ChatChannel.RADIO_UR;
    }

    private String formatMessage(Player sender, ChatChannel channel, String message) {
        String prefix = getPlayerPrefix(sender);
        ChatColor nameColor = getPlayerNameColor(sender);

        return channel.getColor() + "[" + channel.getDisplayName() + "] " +
                prefix + nameColor + sender.getName() +
                ChatColor.WHITE + ": " + message;
    }

    private String getPlayerPrefix(Player player) {
        String teamName = "team_" + player.getName();
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

        if (team != null && team.getPrefix() != null) {
            return team.getPrefix();
        }

        return ChatColor.GRAY + "[玩家] ";
    }

    private ChatColor getPlayerNameColor(Player player) {
        String teamName = "team_" + player.getName();
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

        if (team != null && team.getColor() != null) {
            return team.getColor();
        }

        return ChatColor.WHITE;
    }
}