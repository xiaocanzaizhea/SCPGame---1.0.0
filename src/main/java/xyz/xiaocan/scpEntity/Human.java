package xyz.xiaocan.scpEntity;

import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;

public abstract class Human extends GameEntity{

    public Human(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);

        this.currentShield = 0;
    }

    @Override
    public PointAndTime getKillPoint(){
        return Target.getInstance().getKillPeople();
    }
}
