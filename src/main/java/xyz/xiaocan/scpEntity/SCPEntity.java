package xyz.xiaocan.scpEntity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.configload.option.Target;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;
import xyz.xiaocan.tools.util;

@Getter
@Setter
public abstract class SCPEntity extends GameEntity{

    // SCP特有属性
    // scp移动后就不能回复血量但是可以回复护盾
    // scp受伤后不能回复血量和护盾
    protected boolean canHeal;
    protected double healHpCount;
    protected double healHpNeedTime;
    protected long lastMoveTime = -1;  //用于自然恢复血量

    protected boolean canRecover = false;
    protected double recoverShieldCount;
    protected double recoverShieldNeedTime;
    protected long lastDamagedTime = -1;  //用于自然恢复护盾和血量

    protected BukkitTask healingTask;

    public SCPEntity(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
        this.healHpCount = roleTemplate.getHealHpCount();
        this.healHpNeedTime = roleTemplate.getHealHpNeedTime();

        this.recoverShieldCount = roleTemplate.getRecoverShieldCount();
        this.recoverShieldNeedTime = roleTemplate.getRecoverShieldNeedTime();

        this.currentShield = maxShield;
    }

    protected void healHp(){
        long currentTime = System.currentTimeMillis();
        boolean isMove = util.isOnCooldown(currentTime,
                getLastMoveTime(),
                (long)(getHealHpNeedTime() * 1000));

        boolean isDamaged = util.isOnCooldown(currentTime,
                getLastDamagedTime(),
                (long)(getRecoverShieldNeedTime() * 1000));

        setCanHeal(!isDamaged && !isMove);
        if(canHeal){
            setCurrentHp(currentHp + healHpCount);
//          player.sendRawMessage("§a♥ 血量恢复中...");
        }
    }

    protected void healShield(){

        long currentTime = System.currentTimeMillis();
        boolean isDamaged = util.isOnCooldown(currentTime,
                getLastDamagedTime(),
                (long)(getRecoverShieldNeedTime() * 1000));

        setCanRecover(!isDamaged);
        if(canRecover){
            setCurrentShield(currentShield + recoverShieldCount);
//          player.sendRawMessage("§b🛡 护盾恢复中...");
        }
    }

    @Override
    public PointAndTime getKillPoint(){
        return Target.getInstance().getKillSCP();
    }

    @Override
    public PointAndTime getDamagePoint(){
        return Target.getInstance().getDamageSCP(); //获取SCP被伤害获得的分数
    }

    @Override
    public void dead(Player killer){
        super.dead(killer);

        getPoint(killer, getKillPoint());
    }
}
