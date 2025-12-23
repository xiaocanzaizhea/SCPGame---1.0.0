package xyz.xiaocan.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.doorsystem.FloorContainmentDoor;
import xyz.xiaocan.scpitemstacks.terrainSCP.scp914.SCP914;
import xyz.xiaocan.visual.StickSelectLocation;

import java.util.Map;

public class filltest implements CommandExecutor { //创建scp914
    BoundingBox box;
    BoundingBox box1;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        if(args.length==0){
            if(box==null){
                Location firstLocation = StickSelectLocation.firstLocation;
                Location secondLocation = StickSelectLocation.secondLocation;
                if(firstLocation==null || secondLocation==null){
                    player.sendMessage("先用调试棒定义两个点");
                    return false;
                }

                box = BoundingBox.of(firstLocation, secondLocation);
                Bukkit.getLogger().info("碰撞盒1号创建成功");
                return true;
            }else{
                Location firstLocation = StickSelectLocation.firstLocation;
                Location secondLocation = StickSelectLocation.secondLocation;
                if(firstLocation==null || secondLocation==null){
                    player.sendMessage("先用调试棒定义两个点");
                    return false;
                }

                box1 = BoundingBox.of(firstLocation, secondLocation);
                Bukkit.getLogger().info("碰撞盒2号创建成功");
                return true;
            }
        }else if(args.length==2){
            String id = args[0];
            String id1 = args[1];

            DoorManager instance = DoorManager.getInstance();
            Map<String, FloorContainmentDoor> doors = instance.getDoors();

            FloorContainmentDoor door = doors.getOrDefault(id,null);
            FloorContainmentDoor door1 = doors.getOrDefault(id1,null);
            if(door==null || door1==null){
                sendHelp(player);
                return false;
            }

            if(StickSelectLocation.firstLocation==null){
                player.sendMessage("调试棒没有选取第一个点");
                return false;
            }

            if(box==null){
                player.sendMessage("先创建一个范围盒");
                return false;
            }

            if(box1==null){
                player.sendMessage("请定义第二个范围盒");
                return false;
            }

            Bukkit.getLogger().info("创建scp914成功");
            SCP914.setInstance(new SCP914(door, door1, box, box1));
        }else{
            sendHelp(player);
            return false;
        }

        return true;
    }

    public void sendHelp(Player player){
        player.sendMessage("指令使用出错");
    }
}
