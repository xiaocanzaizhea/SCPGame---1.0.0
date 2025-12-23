package xyz.xiaocan.chatsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Color;

@Getter
public enum ChatChannel {
    GLOBAL("Global", ChatColor.WHITE),
    Admin("Admin", ChatColor.LIGHT_PURPLE),
    LOCAL("Near", ChatColor.GRAY),
    RADIO_SR("Radio", ChatColor.BLUE),
    RADIO_MR("Radio", ChatColor.AQUA),
    RADIO_LR("Radio", ChatColor.GREEN),
    RADIO_UR("Radio", ChatColor.GOLD),
    SCP("Scp", ChatColor.RED),
    OBSERVER("Observer", ChatColor.DARK_PURPLE);

    private final String displayName;
    private final ChatColor color;

    ChatChannel(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }
}
