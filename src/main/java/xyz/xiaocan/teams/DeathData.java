package xyz.xiaocan.teams;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpmanager.TeamManager;

import java.util.UUID;

@Getter
@Setter
public class DeathData { //这个控制玩家死亡后生成死亡信息
    public Location location;
    public ItemDisplay itemDisplay;
    public TextDisplay textDisplay;
    public long deathTime;
    public Player deathPlayer;

    public DeathData(ItemDisplay itemDisplay, TextDisplay textDisplay, long deathTime, Player deathPlayer) {
        this.itemDisplay = itemDisplay;
        this.textDisplay = textDisplay;
        setupTextDisplay(textDisplay);

        this.location = itemDisplay.getLocation();
        this.deathTime = deathTime;
        this.deathPlayer = deathPlayer;

        new BukkitRunnable(){

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long deadTime = currentTime - deathTime;
                if(deadTime >= 20000){
                    this.cancel();
                    textDisplay.remove();
                    return;
                }

                textDisplay.setText("已死亡:" + deadTime / 1000);
            }
        }.runTaskTimer(SCPMain.getInstance(), 0l, 20l);

        TeamManager.getInstance().getAllDeathData().put(UUID.randomUUID(), this);
    }

    private void setupTextDisplay(TextDisplay textDisplay) {
        if (textDisplay == null) return;

        // 设置文字朝向（Billboard）：让文字始终朝向玩家
        textDisplay.setBillboard(Display.Billboard.CENTER);

        // 或者使用固定角度朝向，如果需要的话
        // textDisplay.setBillboard(Display.Billboard.FIXED);

        // 设置文字大小
        textDisplay.setTextOpacity((byte) 127); // 半透明（0-255）

        // 设置背景颜色和透明度
        textDisplay.setBackgroundColor(Color.fromARGB(100, 0, 0, 0)); // 半透明黑色背景

        // 设置对齐方式
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        // 设置行高
        textDisplay.setLineWidth(100); // 设置行宽

        // 设置是否可以看到背面（非单面显示）
        textDisplay.setSeeThrough(true); // 允许透过方块看到

        // 设置是否投射阴影
        textDisplay.setShadowed(true);

        // 设置显示距离
        textDisplay.setViewRange(20f); // 20格内可见
    }

    public void cleanup() {
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.remove();
        }

        if (itemDisplay != null && !itemDisplay.isDead()) {
            itemDisplay.remove();
        }

        // 从管理器中移除
        TeamManager.getInstance().getAllDeathData().values().removeIf(data -> data == this);
    }
}
