package xyz.xiaocan.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.Target;

public class TitleTextTest implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        Bukkit.getLogger().warning(Target.getInstance().toString());

        return true;
    }
}
