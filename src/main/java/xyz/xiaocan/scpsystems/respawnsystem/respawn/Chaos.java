package xyz.xiaocan.scpsystems.respawnsystem.respawn;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BarColor;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.CampModel;

@Getter
@Setter
public class Chaos extends CampModel {
    private static Chaos instance;

    public Chaos() {
        super("Chaos", "§a", BarColor.GREEN);
        this.setTokenCnt(1);
        updateBar();
    }

    public static Chaos getInstance(){
        if(instance==null){
            instance = new Chaos();
        }
        return instance;
    }
}
