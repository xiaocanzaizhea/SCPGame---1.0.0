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
import xyz.xiaocan.elevatorsystem.Elevator;
import xyz.xiaocan.elevatorsystem.ElevatorManager;
import xyz.xiaocan.elevatorsystem.ElevatorState;
import xyz.xiaocan.visual.StickSelectLocation;

import java.util.Map;

public class ElevatorCommand implements CommandExecutor { //创建电梯

    Elevator elevator;
    Elevator elevator1;
    BoundingBox box;
    BoundingBox box1;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;

        DoorManager instance = DoorManager.getInstance();
        Map<String, FloorContainmentDoor> doors = instance.getDoors();
        Map<String, Elevator> elevatorInstances = ElevatorManager.getInstance().ElevatorInstances;

        if(args.length==1){
            if(elevator==null){
                String id = args[0];
                Location firstLocation = StickSelectLocation.firstLocation;
                Location secondLocation = StickSelectLocation.secondLocation;
                if(firstLocation==null || secondLocation==null){
                    player.sendMessage("先用调试棒定义两个点");
                    return false;
                }

                box = BoundingBox.of(firstLocation, secondLocation);
                FloorContainmentDoor door = doors.getOrDefault(id,null);
                elevator = new Elevator(box, door);
                elevatorInstances.put(elevator.id,elevator);
                Bukkit.getLogger().info("电梯一号创建成功");
                return true;

            }else{

                String id = args[0];
                Location firstLocation = StickSelectLocation.firstLocation;
                Location secondLocation = StickSelectLocation.secondLocation;
                if(firstLocation==null || secondLocation==null){
                    player.sendMessage("先用调试棒定义两个点");
                    return false;
                }

                box1 = BoundingBox.of(firstLocation, secondLocation);
                FloorContainmentDoor door = doors.getOrDefault(id,null);
                elevator1 = new Elevator(box1, door);
                elevatorInstances.put(elevator1.id, elevator1);
                Bukkit.getLogger().info("电梯二号创建成功");
                return true;
            }
        }else if(args.length==2){
            String id = args[0];
            String id1 = args[1];

            Elevator ele = elevatorInstances.get(id);
            Elevator ele1 = elevatorInstances.get(id1);

            ele.setLinkElevator(ele1);
            ele1.setLinkElevator(ele);

            ele.getDoor().open();
            ele1.getDoor().close();

            ele.setState(ElevatorState.OPEN);
            ele1.setState(ElevatorState.CLOSED);
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
