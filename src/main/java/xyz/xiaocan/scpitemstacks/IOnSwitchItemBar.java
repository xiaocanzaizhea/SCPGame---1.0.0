package xyz.xiaocan.scpitemstacks;

import org.bukkit.event.player.PlayerItemHeldEvent;

public interface IOnSwitchItemBar {

    void onSwitchItemBar(PlayerItemHeldEvent event, boolean check); //处理声音系统，或者取消任务
}
