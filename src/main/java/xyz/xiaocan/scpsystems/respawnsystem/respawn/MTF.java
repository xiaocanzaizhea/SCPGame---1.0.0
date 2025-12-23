package xyz.xiaocan.scpsystems.respawnsystem.respawn;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BarColor;
import xyz.xiaocan.scpsystems.respawnsystem.respawn.temp.CampModel;

@Getter
@Setter
public class MTF extends CampModel {
    private static MTF instance;

    public MTF() {
        super("§9MTF:", "§9",BarColor.BLUE);
        this.setTokenCnt(2);
        updateBar();
    }

    public static MTF getInstance(){
        if(instance==null){
            instance = new MTF();
        }
        return instance;
    }
}
