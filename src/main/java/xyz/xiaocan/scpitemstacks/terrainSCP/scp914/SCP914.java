package xyz.xiaocan.scpitemstacks.terrainSCP.scp914;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import xyz.xiaocan.doorsystem.DoorLinkType;
import xyz.xiaocan.doorsystem.FloorContainmentDoor;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.visual.StickSelectLocation;

@Getter
@Setter
public class SCP914 {
    private static SCP914 instance;

    public SCP914State currentState = SCP914State.Waiting;

    public SCP914Model scp914Model;

    public BoundingBox boundingBox;
    public BoundingBox boundingBox1;

    public FloorContainmentDoor door;

    public ItemDisplay modelButton;//

    public static NamespacedKey scp914 =
            new NamespacedKey(SCPMain.getInstance(), "scp914");
    public Interaction modelInteraction;
    public Interaction startInteraction;
    public SCP914(FloorContainmentDoor door, FloorContainmentDoor door1,
                  BoundingBox boundingBox, BoundingBox boundingBox1){
        door.setLinkEachOther(door1, DoorLinkType.BOTH);
        this.door = door;
        this.boundingBox = boundingBox;
        this.boundingBox1 = boundingBox1;
        this.scp914Model = SCP914Model.Rough;

        createVisual();
        door.toggle(); //将门初始化为开启
    }

    public void createVisual(){
        Location spawnLocation = StickSelectLocation.firstLocation;
        World world = spawnLocation.getWorld();

        Location upperLevel = spawnLocation.clone().add(0, 1, 0);

        this.modelButton = world.spawn(upperLevel, ItemDisplay.class);
        setupModelButton(this.modelButton);

        this.modelInteraction = world.spawn(upperLevel.clone().add(0,0.05,0), Interaction.class);
        this.startInteraction = world.spawn(upperLevel.clone().add(0,-0.35,0), Interaction.class);

        handleInteraction();
    }

    private void setupModelButton(ItemDisplay display) {
        ItemStack clock = scp914Model.itemStack;

        Transformation transformation = new Transformation(
                new Vector3f(),
                new AxisAngle4f((float)Math.toRadians(180),0,1,0),
                new Vector3f(1.25f,1.25f,1.25f),
                new AxisAngle4f()
        );
        display.setItemStack(clock);

        display.setTransformation(transformation);
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setViewRange(0.5f);
        display.setCustomNameVisible(false);
        display.setCustomName(SCP914Model.Rough.id);
    }

    private void handleInteraction() {
        this.modelInteraction.setInteractionWidth(0.2f);
        this.modelInteraction.setInteractionHeight(0.2f);

        this.startInteraction.setInteractionWidth(0.2f);
        this.startInteraction.setInteractionHeight(0.2f);

        this.modelInteraction.getPersistentDataContainer()
                .set(scp914, PersistentDataType.STRING, "model");
        this.startInteraction.getPersistentDataContainer()
                .set(scp914, PersistentDataType.STRING, "start");
    }

    public void remove(){
        if(getInstance()!=null){
            this.modelButton.remove();
            this.modelInteraction.remove();
            this.startInteraction.remove();
        }
    }

    public static SCP914 getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("scp914实例为空");
            return null;
        }
        return instance;
    }

    public static void setInstance(SCP914 scp914){
        instance = scp914;
    }

    public void changeSCP914State(SCP914State scp914state){
        this.currentState = scp914state;

        if(scp914state==SCP914State.Waiting){ //这里代表结束
            Location colMidLcation1 = getColMidLcation1();
            colMidLcation1.
                    getWorld().
                    playSound(colMidLcation1,
                            Sound.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER,1f,1f);

        }else{ //这里代表开启
            Location location = this.modelButton.getLocation();
            location.
                getWorld().
                playSound(location,Sound.BLOCK_PISTON_EXTEND,1f,1f); //播放开启声音
        }
    }

    public void changeModel() {
        SCP914Model[] models = SCP914Model.values();
        int currentIndex = this.scp914Model.ordinal();
        int nextIndex = (currentIndex + 1) % models.length;
        this.scp914Model = models[nextIndex];

        setupModelButton(this.modelButton);
        Location location = this.modelButton.getLocation();
        location.getWorld().
                playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT,1f,1f); //播放改变模式的声音
    }

    public void toggleSCP914Door() {
        door.toggle(); //切换门状态
    }

    public Location getColMidLcation(){
        double midX = (boundingBox.getMinX() + boundingBox.getMaxX()) / 2;
        double midY = (boundingBox.getMinY() + boundingBox.getMaxY()) / 2;
        double midZ = (boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2;

        return new Location(Bukkit.getWorlds().get(0),midX,midY,midZ);
    }

    public Location getColMidLcation1(){
        double midX = (boundingBox1.getMinX() + boundingBox1.getMaxX()) / 2;
        double midY = (boundingBox1.getMinY() + boundingBox1.getMaxY()) / 2;
        double midZ = (boundingBox1.getMinZ() + boundingBox1.getMaxZ()) / 2;

        return new Location(Bukkit.getWorlds().get(0),midX,midY,midZ);
    }
}
