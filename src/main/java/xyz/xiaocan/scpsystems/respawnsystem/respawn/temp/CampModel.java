package xyz.xiaocan.scpsystems.respawnsystem.respawn.temp;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import xyz.xiaocan.scpsystems.respawnsystem.RespawnSystem;

import java.awt.*;

@Getter
@Setter
public class CampModel {
    private int influence;  //影响力
    private int maxInfluece;

    private int tokenCnt; //大票拥有次数

    private int inTheBigSupportGetPoint = 0;
    //----------bossbar----------
    private int respawnTime;  //计时时间
    private static final int maxTime = 300;
    private BossBar bossBar;

    private BarColor barColor;

    // 字体
    private String name;
    private String styleColor;

    public CampModel(String name, String styleColor, BarColor barColor) {
        this.influence = 0;
        this.maxInfluece = 40;
        this.respawnTime = maxTime; //s

        this.name = name;
        this.styleColor = styleColor;
        String title = styleColor + name + "  §f影响力: " + styleColor +
                influence + "§f/" + maxInfluece
                + "  §f令牌数: " + styleColor + tokenCnt;
        this.bossBar = Bukkit.createBossBar(title, barColor,BarStyle.SOLID);
        this.barColor = barColor;
        updateBar();
    }

    public void addPoint(PointAndTime pointAndTime){
        int point = pointAndTime.getPoint();
        int time = pointAndTime.getTime();

        this.respawnTime += time;

        this.influence += point;
        if(this.influence>=maxInfluece){
            maxInfluece += 40;
            RespawnSystem timer = RespawnSystem.getInstance();
            if(timer.getMaxTokenGet()>0){
                timer.setMaxTokenGet(timer.getMaxTokenGet()-1);
                tokenCnt += 1;
            }
        }

        updateBar();
    }

    public void updateBar(){
        String title = styleColor + name + "  §f影响力: " + styleColor +
                influence + "§f/" + maxInfluece
                + "  §f令牌数: " + styleColor + tokenCnt;
        float progress = Math.max(0, (float)respawnTime / (float)maxTime);

        bossBar.setTitle(title);
        bossBar.setProgress(progress);
    }

    public void addBarToPlayer(Player player){
        this.bossBar.addPlayer(player);
    }

    public void removeBarFromPlayer(Player player){
        this.bossBar.removePlayer(player);
    }

    public void subTime(){
        this.respawnTime -= 1;
        updateBar();
    }
}
