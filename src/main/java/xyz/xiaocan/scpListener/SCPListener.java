package xyz.xiaocan.scpListener;

import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.scoreboard.Team;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpsystems.phasecontroller.PhaseController;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.DeathData;

import java.util.Random;


/**
 * 这个监听器专注处理玩家通用的数据
 */
public class SCPListener implements Listener {

    public SCPListener(){}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getPlayer().getGameMode()!=GameMode.CREATIVE){
            event.setCancelled(true);
        }
    }

    /**
     * 取消摔落伤害
     * @param event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }

        if(cause == EntityDamageEvent.DamageCause.SUFFOCATION){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinEvent event){
        try{
            Player joinPlayer = event.getPlayer();
//
            //初始玩家状态
            PhaseController.getInstance().handlePlayerJoin(joinPlayer);

            event.setJoinMessage(null);

            Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
                String teamName = "team_" + joinPlayer.getName();
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

                if (team != null) {
                    String prefix = team.getPrefix();
                    String coloredName = team.getColor() + joinPlayer.getName();
                    String joinMessage = prefix + coloredName + " §a加入了游戏";
                    Bukkit.broadcastMessage(joinMessage);
                } else {
                    Bukkit.broadcastMessage(joinPlayer.getDisplayName() + " §a加入了游戏");
                }
            }, 2L);

        }catch (Exception e){
            Bukkit.getLogger().severe("处理玩家加入事件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler
    public void OnPlayerDamage(EntityDamageByEntityEvent event){
        //暂时注解，测试scp攻击

//        if (event.getDamager() instanceof Player
//                && event.getEntity() instanceof Player) {
//            event.setCancelled(true);
//        }
    }

    @EventHandler
    public void onPlayerQuitGame(PlayerQuitEvent event){
        Player quitPlayer = event.getPlayer();

        event.setQuitMessage(null);

        Bukkit.getScheduler().runTaskLater(SCPMain.getInstance(), () -> {
            String teamName = "team_" + quitPlayer.getName();
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

            if (team != null) {
                String prefix = team.getPrefix();
                String coloredName = team.getColor() + quitPlayer.getName();
                String joinMessage = prefix + coloredName + " §c退出了游戏";
                Bukkit.broadcastMessage(joinMessage);
            } else {
                Bukkit.broadcastMessage(quitPlayer.getDisplayName() + " §c退出了游戏");
            }
        }, 2L);

        //处理玩家退出
        PhaseController.getInstance().handlePlayerQuit(quitPlayer);
    }

    /**
     * 玩家死亡后数据处理
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();

        event.setDroppedExp(0);
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);

        ItemDisplay itemDisplay = Bukkit.getWorlds().get(0).spawn(
                player.getLocation().add(0,0.5,0), ItemDisplay.class);
        TextDisplay textDisplay = Bukkit.getWorlds().get(0).spawn(
                player.getLocation().add(0,1.0,0), TextDisplay.class);

        itemDisplay.setItemStack(Material.PLAYER_HEAD.asItemType().createItemStack());
        new DeathData(itemDisplay, textDisplay,System.currentTimeMillis(), player);
    }
    //处理玩家被T后TeamManager的数据
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){

        Player player = event.getPlayer();
        TeamManager teamManager = TeamManager.getInstance();

        teamManager.getAllPlayersMapping().remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event){
        if(event.getClickedInventory() == null) {
            return;
        }

        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if(!TeamManager.getInstance().getAllPlayersMapping().containsKey(player.getUniqueId())){
            return;
        }

        if(event.getClickedInventory().getType() == InventoryType.PLAYER){
            int slot = event.getSlot();

            if((slot > 8 && slot < 36) || slot == 4){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerPick(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player){
            event.setCancelled(true);
        }
    }

    /**
     * 在指定位置的XZ平面范围内生成随机位置
     * @param center 中心位置
     * @param radius 半径（格）
     * @return 随机位置
     */
    private Location getRandomLocationAround(Location center, double radius) {
        World world = center.getWorld();
        if (world == null) return center.clone();

        Random random = new Random();

        double offsetX = (random.nextDouble() - 0.5) * 2 * radius; // -radius 到 +radius
        double offsetZ = (random.nextDouble() - 0.5) * 2 * radius;

        Location randomLocation = center.clone().add(offsetX, 0, offsetZ);

        randomLocation = findSafeLocation(randomLocation, world);

        return randomLocation;
    }

    /**
     * 寻找安全的位置（不在方块内）
     */
    private Location findSafeLocation(Location location, World world) {
        if (isLocationSafe(location, world)) {
            return location;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location testLocation = location.clone().add(x, 0, z);
                if (isLocationSafe(testLocation, world)) {
                    return testLocation;
                }
            }
        }

        return location.clone().add(0, 1, 0);
    }

    /**
     * 检查位置是否安全（不在固体方块内）
     */
    private boolean isLocationSafe(Location location, World world) {
        return !world.getBlockAt(location).getType().isSolid() &&
                !world.getBlockAt(location.clone().add(0, 1, 0)).getType().isSolid();
    }
}
