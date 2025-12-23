package xyz.xiaocan.scpgame;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xiaocan.commands.*;
import xyz.xiaocan.commands.debug.ChangeRole;
import xyz.xiaocan.configload.ConfigManager;
import xyz.xiaocan.configload.option.scpoption.SCP914Setting;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.dropitemsystem.DropManager;
import xyz.xiaocan.scpListener.*;
import xyz.xiaocan.scpitemstacks.card.snake.RankBoard;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.scpsystems.SCPManager;

@Getter
@Setter
public class SCPMain extends JavaPlugin {

    private static SCPMain instance;

    private SCPManager scpManager;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        //必须先对配置文件进行初始化和读取!!
        configManager = ConfigManager.getInstance();
        scpManager = SCPManager.getInstance();

        // 配置文件的初始化，初始一个配置文件和读取配置文件
        configManager.init();
        scpManager.init();

        loadConfig();
        registerListeners();
        registerCommands();

        getLogger().info("debug Mode: " + ScpOption.getInstance().isDebug());
        getLogger().info("所有配置文件加载完成!");

        Bukkit.getLogger().warning(SCP914Setting.getInstance().toString());

        RankBoard.getInstance();
    }

    @Override
    public void onDisable() {
        removeData();

        if (RankBoard.getInstance() != null) {
            RankBoard.getInstance().onDisable();
        }
    }

    public void registerListeners(){
        //注册事件
        getServer().getPluginManager().registerEvents(new SCPListener(),this);
        getServer().getPluginManager().registerEvents(new DoorListener(), this);
        getServer().getPluginManager().registerEvents(new Scp173Listener(), this);
        getServer().getPluginManager().registerEvents(new DropAndPickUpItemListener(), this);
        getServer().getPluginManager().registerEvents(new Scp914Listener(), this);
        getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
        getServer().getPluginManager().registerEvents(new Scp330Listener(), this);
        getServer().getPluginManager().registerEvents(new SCPItemListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    public void registerCommands(){
        //注册命令
        getCommand("scpdoor").setExecutor(new DoorCommand());
        getCommand("test").setExecutor(new ScptestCommands());
        getCommand("gun").setExecutor(new GunTest());
        getCommand("scpfill").setExecutor(new filltest());
        getCommand("scpEle").setExecutor(new ElevatorCommand());
        getCommand("snake").setExecutor(new Snake());
        getCommand("titletest").setExecutor(new TitleTextTest());
        getCommand("scpCR").setExecutor(new ChangeRole());
    }

    public void loadConfig(){
        DoorManager.getInstance().loadDoorsInstance();  //读取门的实例
    }

    public static SCPMain getInstance(){
        return instance;
    }

    public void removeData(){
        DoorManager.getInstance().removeAllDoorData();  //移除门display模型
        DropManager.getInstance().removeAllDropItem();  //移除所有掉落物
        TeamManager.getInstance().clearUpDeathData();   //移除所有死亡消息的itemdisplay
    }

}
