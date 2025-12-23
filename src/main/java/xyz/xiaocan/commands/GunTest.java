package xyz.xiaocan.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.scpoption.SCP173SpiecalSetting;
import xyz.xiaocan.scpitemstacks.ItemManager;

import java.awt.event.InputEvent;

public class GunTest implements CommandExecutor {
    boolean c = true;
    Interaction interaction;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;

        if(c){
            player.setAllowFlight(true);
            interaction = player.getLocation().getWorld().spawn(player.getLocation(),Interaction.class);
            interaction.setInteractionHeight(1);
            interaction.setInteractionWidth(1);
            c=false;
        }else{

            player.setAllowFlight(false);
            interaction.remove();
            c=true;
        }

        return true;
    }
}
