package xyz.xiaocan.scpListener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import xyz.xiaocan.doorsystem.FloorContainmentDoor;
import xyz.xiaocan.dropitemsystem.DropItem;
import xyz.xiaocan.dropitemsystem.DropManager;
import xyz.xiaocan.elevatorsystem.Elevator;
import xyz.xiaocan.elevatorsystem.ElevatorManager;
import xyz.xiaocan.elevatorsystem.ElevatorState;
import xyz.xiaocan.scpgame.SCPMain;

import java.util.Map;
import java.util.UUID;

public class ElevatorListener implements Listener {
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){

        if (!(event.getRightClicked() instanceof Interaction)) {
            return;
        }

        Interaction interaction = (Interaction) event.getRightClicked();
        String ele = interaction.getPersistentDataContainer()
                .get(Elevator.elevator_id, PersistentDataType.STRING);

        if(ele==null){
            return;
        }

        Elevator elevator = ElevatorManager.getInstance().ElevatorInstances.get(ele);

        if(elevator==null){
            return;
        }

        //获取电梯，处理逻辑
//        elevator.setState(ElevatorState.Open);  //初始两台电梯状态
//        elevator.getElevator().setState(ElevatorState.Closed);
        runningElevator(elevator);
    }

    public void runningElevator(Elevator elevator){
        if(elevator.getState()==ElevatorState.RUNNING
                || elevator.getOtherElevator().getState()==ElevatorState.RUNNING){
            return;
        }
        ElevatorState originState = elevator.getState();

        //关门
        closeAllDoor(elevator);
        Bukkit.getLogger().warning("关闭了所有门");

        //设置两台电梯为运行中,并且更新视觉效果
        setStateAndVisual(elevator,ElevatorState.RUNNING, ElevatorState.RUNNING);

        new BukkitRunnable(){

            @Override
            public void run() {
                //传送所有玩家和物品
                handlePlayerAndItemTeleport(elevator);
                Bukkit.getLogger().warning("电梯传送玩家和物品成功");
            }
        }.runTaskLater(SCPMain.getInstance(),elevator.getDoor().getDoorTemplate().getAnimationTickTime()); //设置为门关上后再传送

        //数秒后开门,然后恢复电梯状态
        new BukkitRunnable(){
            @Override
            public void run() {
                //更新状态，更新视觉效果
                setStateAndVisual(elevator,
                        ElevatorState.valueOf(originState.invert),
                        originState);
                //开门
                toggleDoor(elevator, originState);
            }
        }.runTaskLater(SCPMain.getInstance(),(long)(elevator.getElevatorSetting().runningTime * 20));
    }

    public void closeAllDoor(Elevator elevator){
        FloorContainmentDoor door = elevator.door;
        Elevator otherElevator = elevator.getOtherElevator();

        door.close();
        otherElevator.door.close();
    }

    public void toggleDoor(Elevator elevator, ElevatorState origin){
        if(origin==ElevatorState.CLOSED){
            elevator.door.open();
            elevator.getOtherElevator().door.close();
        }else{
            elevator.door.close();
            elevator.getOtherElevator().door.open();
        }
    }

    public void setStateAndVisual(Elevator elevator, ElevatorState elevatorState, ElevatorState elevatorState1){
        Elevator elevator1 = elevator.getOtherElevator();

        elevator.setState(elevatorState);
        elevator1.setState(elevatorState1);

        elevator.setDoorVisual();
        elevator1.setDoorVisual();
    }

    private Location getMappingLocation(Location location, Elevator elevator) {
        BoundingBox box = elevator.getBox();
        BoundingBox box1 = elevator.getOtherElevator().getBox();

        double newX = box1.getMinX() + (location.getX() - box.getMinX());
        double newY = box1.getMinY() + (location.getY() - box.getMinY());
        double newZ = box1.getMinZ() + (location.getZ() - box.getMinZ());

        Location newLoc =
                new Location(location.getWorld(),
                        newX, newY, newZ, location.getYaw(),location.getPitch());
        return newLoc;
    }

    public void handlePlayerAndItemTeleport(Elevator elevator){
        BoundingBox boundingBox = elevator.getBox();
        Map<UUID, DropItem> dropItemMap = DropManager.getInstance().getDropItemMap();

        for (Player player: Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();
            if(!boundingBox.contains(playerLocation.toVector())){
                continue;
            }

            Location mappingLocation = getMappingLocation(playerLocation, elevator);
            player.teleport(mappingLocation);
        }

        for (DropItem dropItem:dropItemMap.values()) {
            Location location = dropItem.getLocation();

            if(!boundingBox.contains(location.toVector())){
                continue;
            }

            Location mappingLocation = getMappingLocation(location, elevator);
            dropItem.teleport(mappingLocation);
        }
    }
}
