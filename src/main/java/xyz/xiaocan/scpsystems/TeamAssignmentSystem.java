package xyz.xiaocan.scpsystems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.RifleMan;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.Chaos.DCLASS;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.Guard;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.MTFPrivate;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.MTF.Scientist;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP049.SCP_049;
import xyz.xiaocan.scpEntity.scpGameEntityInstance.SCP.SCP173.SCP_173;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.HumanType;
import xyz.xiaocan.teams.roletypes.ScpType;

import java.util.*;
import java.util.stream.Collectors;

public class TeamAssignmentSystem {
    private static TeamAssignmentSystem instance;
    private TeamManager teamManager;
    private final Random random = new Random();

    private TeamAssignmentSystem(){
        this.teamManager = TeamManager.getInstance();
    }

    /**
        自动处理玩家身份
     */
    public void autoAssignTeams(List<Player> players){
        int totalPlayers = players.size();

        // 计算各队伍分配比例
        int scpCount = Math.max(1, totalPlayers / 6); // SCP占总人数的1/6
        int scientistCount = Math.max(1,totalPlayers / 6);        // 科学家占1/6
        int guardCount = Math.max(1,totalPlayers / 6) ;            // 警卫占1/6
        int dClassCount = totalPlayers - scpCount - scientistCount - guardCount;           // 剩余的为D级

        Collections.shuffle(players);

        int index = 0;

        for (int i = 0; i < scpCount && index < players.size(); i++, index++) {
            assignSCPRole(players.get(index));
        }

        for (int i = 0; i < scientistCount && index < players.size(); i++, index++) {
            assignRole(players.get(index), HumanType.SCIENTIST);
        }

        for (int i = 0; i < guardCount && index < players.size(); i++, index++) {
            assignRole(players.get(index), HumanType.GUARD);
        }

        for (int i = 0; i < dClassCount && index < players.size(); i++, index++) {
            assignRole(players.get(index), HumanType.DCLASS);
        }
    }

    /**
        分配SCP角色
    */
    private void assignSCPRole(Player player) {
        List<ScpType> availableScpTypes = Arrays.stream(ScpType.values())
                .filter(type -> type != ScpType.SCP0492)
                .collect(Collectors.toList());

        ScpType scpType = availableScpTypes.get(random.nextInt(availableScpTypes.size()));
        RoleTemplate roleTemplate = teamManager.getRolesTemplates().get(scpType);

        switch (scpType) {
            case SCP173:
                new SCPPlayer(player,
                        new SCP_173(player, roleTemplate), scpType);
                break;
            case SCP049:
                new SCPPlayer(player,
                        new SCP_049(player, roleTemplate), scpType);
                break;
        }
    }

    /**
     * 分配人类角色
    */
    private void assignRole(Player player, HumanType human) {
        RoleTemplate roleTemplate = teamManager.getRolesTemplates().get(human);

        switch (human) {
            case DCLASS:
                new SCPPlayer(player,
                        new DCLASS(player, roleTemplate),
                        human);
                break;
            case SCIENTIST:
                new SCPPlayer(player,
                        new Scientist(player,roleTemplate),
                        human);
                break;
            case GUARD:
                new SCPPlayer(player,
                        new Guard(player,roleTemplate),
                        human);
                break;
            default:
        }

    }

    public static TeamAssignmentSystem getInstance(){
        if(instance==null){
            instance = new TeamAssignmentSystem();
        }
        return instance;
    }
}
