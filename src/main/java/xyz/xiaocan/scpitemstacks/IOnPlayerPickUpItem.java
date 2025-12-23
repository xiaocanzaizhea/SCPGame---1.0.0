package xyz.xiaocan.scpitemstacks;

import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface IOnPlayerPickUpItem {
    public void onPlayerPickItem(PlayerInteractEntityEvent event);
}
