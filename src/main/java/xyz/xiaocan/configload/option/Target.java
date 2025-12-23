package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.PointAndTime;

@Getter
@Setter
public class Target {
    private static Target instance;

    private PointAndTime killSCP;       //击杀scp
    private PointAndTime killSCP0492;   //击杀小姜
    private PointAndTime damageSCP;     //对scp造成伤害(10%)
    private PointAndTime useSpeicalItemKillSCP;   //使用特殊武器杀死scp
    private PointAndTime firstUseSCPItem;    //不消耗scp物品首次被使用
    private PointAndTime useConsumeSCPItem;  //消耗scp使用
    private PointAndTime killPeople;    //击杀敌对阵营
    private PointAndTime activateGenerator;  //激活发电机
    private PointAndTime killUnarmedPeople;  //杀死未武装人员

    //------------九尾狐目标--------------
    private PointAndTime scientistEscape;    //科学家逃离
    private PointAndTime bindingDclassEscape;  //捆绑D逃离

    //------------混沌目标----------------
    private PointAndTime dclassEscape;  //混沌目标， D级逃离

    public static Target getInstance(){
        if(instance==null){
            return new Target();
        }
        return instance;
    }

    public static void setInstance(Target target){
        instance = target;
    }

    @Override
    public String toString() {
        return "Target{" +
                "killSCP=" + killSCP +
                ", killSCP0492=" + killSCP0492 +
                ", damageSCP=" + damageSCP +
                ", useSpeicalItemKillSCP=" + useSpeicalItemKillSCP +
                ", firstUseSCPItem=" + firstUseSCPItem +
                ", useConsumeSCPItem=" + useConsumeSCPItem +
                ", killPeople=" + killPeople +
                ", activateGenerator=" + activateGenerator +
                ", killUnarmedPeople=" + killUnarmedPeople +
                ", scientistEscape=" + scientistEscape +
                ", bindingDclassEscape=" + bindingDclassEscape +
                ", dclassEscape=" + dclassEscape +
                '}';
    }
}
