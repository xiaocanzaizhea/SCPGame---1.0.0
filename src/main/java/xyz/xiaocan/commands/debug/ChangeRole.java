package xyz.xiaocan.commands.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpEntity.Admin;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.DCLASS;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.RifleMan;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.Guard;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.MTFPrivate;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.Scientist;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049.SCP_049;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP106.SCP_106;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP173.SCP_173;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.teams.roletypes.HumanType;
import xyz.xiaocan.teams.roletypes.ScpType;

import java.util.Map;
import java.util.UUID;

public class ChangeRole implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player player){
            if(strings.length!=2){
                player.sendMessage("使用方法SCPCR playerName role");
                return false;
            }

            String playerName = strings[0];
            Player target = Bukkit.getPlayer(playerName);
            if (target != null && target.isOnline()) {}
            else {
                player.sendMessage(ChatColor.RED + "该玩家不在线");
                return false;
            }

            String role = strings[1];

            Map<UUID, SCPPlayer> allPlayersMapping = TeamManager.getInstance().getAllPlayersMapping();
            if(allPlayersMapping.containsKey(target.getUniqueId())){
                allPlayersMapping.remove(target.getUniqueId());
            }

            RoleTemplate roleTemplate = null;
            switch(role){
                case "scp049":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(ScpType.SCP049);
                    new SCPPlayer(target,
                            new SCP_049(player, roleTemplate), ScpType.SCP049);
                    break;
                case "scp173":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(ScpType.SCP173);
                    new SCPPlayer(target,
                            new SCP_173(player, roleTemplate), ScpType.SCP173);
                    break;
                case "scp106":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(ScpType.SCP106);
                    new SCPPlayer(target,
                            new SCP_106(player, roleTemplate), ScpType.SCP106);
                    break;
                case "dclass":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.DCLASS);
                    new SCPPlayer(target,
                            new DCLASS(player, roleTemplate), HumanType.DCLASS);
                    break;
                case "mtf":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.MTFPRIVATE);
                    new SCPPlayer(target,
                            new MTFPrivate(player, roleTemplate), HumanType.MTFPRIVATE);
                    break;
                case "mtf1":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.MTFPSERGEANT);
                    new SCPPlayer(target,
                            new MTFPrivate(player, roleTemplate), HumanType.MTFPSERGEANT);
                    break;
                case "mtf2":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.MTFCAPTAIN);
                    new SCPPlayer(target,
                            new MTFPrivate(player, roleTemplate), HumanType.MTFCAPTAIN);
                    break;
                case "chaos":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.RIFLEMAN);
                    new SCPPlayer(target,
                            new RifleMan(player, roleTemplate), HumanType.RIFLEMAN);
                    break;
                case "chaos1":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.PREDATOR);
                    new SCPPlayer(target,
                            new RifleMan(player, roleTemplate), HumanType.PREDATOR);
                    break;
                case "chaos2":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.SUPPRESSOR);
                    new SCPPlayer(target,
                            new RifleMan(player, roleTemplate), HumanType.SUPPRESSOR);
                    break;
                case "guard":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.GUARD);
                    new SCPPlayer(target,
                            new Guard(player, roleTemplate), HumanType.GUARD);
                    break;
                case "scientist":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.SCIENTIST);
                    new SCPPlayer(target,
                            new Scientist(player, roleTemplate), HumanType.SCIENTIST);
                    break;
                case "admin":
                    roleTemplate = TeamManager.getInstance().getRolesTemplates().get(HumanType.GUARD);
                    new SCPPlayer(target,
                            new Admin(player, roleTemplate), HumanType.Admin);
                    break;
            }
        }

        return false;
    }
}
