package xyz.xiaocan.scpitemstacks.weapon.grenade;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.itemoption.grenade.GrenadeTemp;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.IOnLeftClick;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;
import xyz.xiaocan.tools.progressBar;

@Setter
@Getter
public class Grenade extends AbstractGrenade
        implements IOnRightClick, IOnLeftClick, IOnSwitchItemBar {

    //两种爆炸，一种丢出去爆炸，一种玩家死后掉地上爆炸

    private GrenadeTemp grenadeTemp;

    private double damage;

    private boolean isready = false;

    private BukkitTask pullTask;
    private BukkitTask bombTask;
    public Grenade(GrenadeTemp grenadeTemp) {
        super(grenadeTemp.getId(), grenadeTemp.getDisplayName(),
                grenadeTemp.getMaterial(), grenadeTemp.getCustomModelData(),
                grenadeTemp.getLore(), grenadeTemp.getRadius(), grenadeTemp.getExplosionTime());

        this.grenadeTemp = grenadeTemp;
        this.damage = grenadeTemp.getDamage();
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        if(isready){ //取消
            isready = false;
            cancelTask(event.getPlayer(), pullTask);
        }else{
            pullTask = new BukkitRunnable(){
                float t = 0;
                @Override
                public void run() {
                    if((t+=0.05)>=3){
                        this.cancel();
                        isready = true;
                        return;
                    }

                    progressBar.updateUseProgress(event.getPlayer(), t,3.0f, "手雷准备中");
                }
            }.runTaskTimer(SCPMain.getInstance(),0l,1l);
        }
    }

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {
        //丢出

    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(isready){ //拉起的手雷禁止切换其他物品栏
            event.setCancelled(true);
        }
    }
}
