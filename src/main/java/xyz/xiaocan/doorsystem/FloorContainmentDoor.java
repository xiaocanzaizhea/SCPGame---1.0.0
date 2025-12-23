package xyz.xiaocan.doorsystem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.configload.option.DoorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class FloorContainmentDoor extends ContainmentDoor {
    private Map<FloorContainmentDoor, DoorLinkType> linkedDoorMap;

    public FloorContainmentDoor(String id, Location origin, Location second, int width, int height,
                                int depth, double moveDistance, DoorTemplate doorTemplate, Player player) {
        super(id, origin, second, width, height,
                depth, moveDistance, doorTemplate);
        this.linkedDoorMap = new HashMap<>();

        initializeDoorBlocksAndBoundbox(player);

        if(ScpOption.getInstance().isDebug()){
            this.textDisplay = createTextDisplay();
            this.task = new BukkitRunnable(){
                @Override
                public void run() {
                    displayCollisionBoxParticles(getCollisionBox());
                }
            }.runTaskTimer(SCPMain.getInstance(),0L, 20L);
        }

        if(doorTemplate.isNeedIcon()){
            List<ItemDisplay> icons = createIcons(player);
            this.icons = icons;
            iconsOriginLoc.add(icons.get(0).getLocation());
            iconsOriginLoc.add(icons.get(1).getLocation());
        }

        if(doorTemplate.getDoorLock()!=null){
            createToggleButton(player);
        }
    }

    private void initializeDoorBlocksAndBoundbox(Player player) {

        Location pos1 = this.first.clone();
        Location pos2 = this.second.clone();

        collisionBox = BoundingBox.of(pos1, pos2);

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        BlockFace blockFace = player.getFacing();
        if(blockFace==BlockFace.EAST){
            moveDirection = new Vector(0,0,1);
        }else if(blockFace==BlockFace.NORTH){
            moveDirection = new Vector(1,0,0);
        }else if(blockFace==BlockFace.SOUTH){
            moveDirection = new Vector(-1,0,0);
        }else if(blockFace==BlockFace.WEST){
            moveDirection = new Vector(0,0,-1);
        }

        // 遍历并填充每个方块
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location blockLoc = new Location(pos1.getWorld(), x, y, z);
                    blockLoc.getBlock().setType(doorTemplate.getMaterial());
                    doorBoundBlocks.add(blockLoc.getBlock());

                    BlockDisplay blockDispaly = createBlockDispaly(blockLoc);
                    movingDisplays.add(blockDispaly);
                }
            }
        }
    }

    protected void createToggleButton(Player player){
        List<ItemDisplay> itemDisplays = new ArrayList<>();
        List<Interaction> interactions = new ArrayList<>();
        double yLoc = first.clone().getY()+1.5;

        double zLoc = second.clone().getZ();
        double xLoc = second.clone().getX();

        double zLoc1 = first.clone().getZ();
        double xLoc1 = first.clone().getX();

        BlockFace blockFace = player.getFacing();
        AxisAngle4f rotation = new AxisAngle4f(0,0,0,0);
        AxisAngle4f rotation1 = new AxisAngle4f(0,0,0,0);
        if(blockFace==BlockFace.EAST){
            zLoc+=1.5;

            zLoc1-=0.5;
            xLoc1+=1;
            rotation = new AxisAngle4f((float)Math.toRadians(-90),0,1,0);
            rotation1 = new AxisAngle4f((float)Math.toRadians(-180),0,1,0);
        }else if(blockFace==BlockFace.WEST){
            zLoc-=0.5;
            xLoc+=1;

            zLoc1+=1.5;
            rotation = new AxisAngle4f((float)Math.toRadians(-270),0,1,0);
            rotation1 = new AxisAngle4f((float)Math.toRadians(-180),0,1,0);
        }else if(blockFace==BlockFace.SOUTH){
            xLoc-=0.5;

            zLoc1+=1;
            xLoc1+=1.5;
            rotation = new AxisAngle4f((float)Math.toRadians(-180),0,1,0);
            rotation1 = new AxisAngle4f((float)Math.toRadians(-180),0,1,0);
        }else if(blockFace==BlockFace.NORTH){
            zLoc+=1;
            xLoc+=1.5;

            xLoc1-=0.5;
            rotation1 = new AxisAngle4f((float)Math.toRadians(-180),0,1,0);
        }

        Location location = new Location(  //这两个点将生成 button
                Bukkit.getWorlds().get(0),xLoc,yLoc,zLoc);

        Location location1 = new Location(
                Bukkit.getWorlds().get(0), xLoc1,yLoc,zLoc1);

        ItemDisplay toggleButton = location.getWorld().spawn(location, ItemDisplay.class);
        ItemDisplay toggleButton1 = location1.getWorld().spawn(location1, ItemDisplay.class);

        Interaction interaction = location.getWorld().
                spawn(location.add(0,-0.15,0), Interaction.class);
        Interaction interaction1 = location1.getWorld().
                spawn(location1.add(0,-0.15,0), Interaction.class);

        setDispaly(toggleButton,doorTemplate.getDoorLock().id);
        setDispaly(toggleButton1,doorTemplate.getDoorLock().id);

        float size = (float)doorTemplate.getDoorLock().size;
        Transformation transformation = new Transformation(
                new Vector3f(0,0,0),
                rotation,
                new Vector3f(size,size,size),
                new AxisAngle4f(0,0,0,0)
        );
        toggleButton.setTransformation(transformation);

        transformation.getRightRotation().set(rotation1);
        toggleButton1.setTransformation(transformation);

        interaction.setInteractionHeight(0.5f);
        interaction.setInteractionWidth(0.4f);

        interaction1.setInteractionHeight(0.5f);
        interaction1.setInteractionWidth(0.4f);

        //interaction
        interactions.add(interaction);
        interactions.add(interaction1);

        itemDisplays.add(toggleButton);
        itemDisplays.add(toggleButton1);

        interaction.getPersistentDataContainer().set(
                door_id, PersistentDataType.STRING, id);

        interaction1.getPersistentDataContainer().set(
                door_id, PersistentDataType.STRING, id);

        this.toggleButtons = itemDisplays;
        this.interactions = interactions;
    }
    protected List<ItemDisplay> createIcons(Player player){
        List<ItemDisplay> itemDisplays1 = new ArrayList<>();
        double xMid = (first.getX() + second.getX()) / 2;
        double yMid = (first.getY() + second.getY()) / 2;
        double zMid = (first.getZ() + second.getZ()) / 2;

        Location iconLocation = new Location(first.getWorld(), xMid,yMid,zMid);
        iconLocation.add(0.5,0.5,0.5);
        ItemDisplay spawn = iconLocation.getWorld().spawn(iconLocation, ItemDisplay.class);
        ItemDisplay spawn1 = iconLocation.getWorld().spawn(iconLocation, ItemDisplay.class);

        itemDisplays1.add(spawn);
        itemDisplays1.add(spawn1);

        setDispaly(spawn,0);
        setDispaly(spawn1,0);

        Vector3f tranlation = null;
        Vector3f tranlation1 = null;

        BlockFace blockFace = player.getFacing();
        AxisAngle4f rotation = new AxisAngle4f(0,0,0,0);
        if(blockFace==BlockFace.EAST){
            tranlation = new Vector3f(0.15f,0,0);
            tranlation1 = new Vector3f(-0.15f,0,0);
            rotation = new AxisAngle4f((float)Math.toRadians(90),0,1,0);
        }else if(blockFace==BlockFace.WEST){
            tranlation = new Vector3f(0.15f,0,0);
            tranlation1 = new Vector3f(-0.15f,0,0);
            rotation = new AxisAngle4f((float)Math.toRadians(-90),0,1,0);
        }else if(blockFace==BlockFace.SOUTH){
            tranlation = new Vector3f(0,0,0.15f);
            tranlation1 = new Vector3f(0,0,-0.15f);
        }else if(blockFace==BlockFace.NORTH){
            tranlation = new Vector3f(0,0,0.15f);
            tranlation1 = new Vector3f(0,0,-0.15f);
        }

        Transformation transformation = new Transformation(
                tranlation,
                rotation,
                new Vector3f(0.3f,0.3f,0.3f),
                new AxisAngle4f(0,0,0,0)
        );
        spawn.setTransformation(transformation);

        transformation.getTranslation().set(tranlation1);
        spawn1.setTransformation(transformation);
        return itemDisplays1;
    }

    protected void setDispaly(ItemDisplay dispaly, int custommodeldata){
        ItemStack itemStack = new ItemStack(Material.COAL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(custommodeldata);
        itemStack.setItemMeta(meta);

        dispaly.setItemStack(itemStack);
        dispaly.setPersistent(false);
        dispaly.setBrightness(new Display.Brightness(15,15));
        dispaly.setTeleportDuration(5);
    }

    public BlockDisplay createBlockDispaly(Location location){

        BlockDisplay blockDisplay = location.getWorld().spawn(location,BlockDisplay.class);
        blockDisplay.setBlock(doorTemplate.getMaterial().createBlockData());
        blockDisplay.setBrightness(new Display.Brightness(15,15));
        blockDisplay.setTeleportDuration(5);
        blockDisplay.setPersistent(false);

        float xscale = moveDirection.getX()!=0?1f:(float)doorTemplate.getItemDisplaySize();
        float zscale = moveDirection.getZ()!=0?1f:(float)doorTemplate.getItemDisplaySize();

        float xoffest = moveDirection.getX()==0?0.37f:0;
        float zoffest = moveDirection.getZ()==0?0.37f: 0;
        Vector3f translation = new Vector3f(xoffest,0,zoffest);

        Vector3f scale = new Vector3f(xscale,1,zscale);
        Transformation transformation = new Transformation(
                translation,
                new AxisAngle4f(0,0,0,0),
                scale,
                new AxisAngle4f(0,0,0,0)
        );

        blockDisplay.setTransformation(transformation);
        return blockDisplay;
    }
    /**
     *切换门的状态
     */
    public void toggle() {
        if (state == DoorState.OPENING || state == DoorState.CLOSING) {
            return;
        }

        handleLinkDoors(state); //先处理链接的门
        if (state == DoorState.CLOSED) {
            open();
        } else {
            if(doorTemplate.getAutoClosed()>=0) return; //自动关门,禁止手动关门
            close();
        }
    }
    public void open() {
        if(state==DoorState.OPEN || state==DoorState.OPENING)return;

        double autoClosed = doorTemplate.getAutoClosed();

        try{
            Sound sound = doorTemplate.getOpenSoundEffect();
            first.getWorld().playSound(first, sound,
                    1.5f, 1.2f);
        }catch (IllegalArgumentException e){
            first.getWorld().playSound(first, Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 2f);
            Bukkit.getLogger().warning("门类型 " + doorTemplate.getId() + " 的声音配置错误: " + doorTemplate.getOpenSoundEffect());
        }

        state = DoorState.OPENING;
        animateDoor(true);

        if(autoClosed>=0){
            new BukkitRunnable(){
                @Override
                public void run() {
                    close();
                }
            }.runTaskLater(SCPMain.getInstance(), (long)(autoClosed * 20));
        }
    }
    public void close() {
        if(state==DoorState.CLOSED || state==DoorState.CLOSING)return;

        try{
            Sound sound = doorTemplate.getClosedSoundEffect();
            first.getWorld().playSound(first, sound,
                    1.5f, 0.8f);
        }catch (IllegalArgumentException e){
            first.getWorld().playSound(first, Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 2f);
            Bukkit.getLogger().warning("门类型 " + doorTemplate.getId() + " 的声音配置错误: " + doorTemplate.getClosedSoundEffect());
        }

        state = DoorState.CLOSING;
        animateDoor(false);
    }
    public void showDoorBoundBox(){ //显示屏障方块，也就是门消失
        Material material;
        if(doorTemplate.getItemDisplaySize()>=0.5){
            material = Material.BARRIER;
        }else{
            material = Material.PINK_STAINED_GLASS_PANE;
        }

        for (Block block : doorBoundBlocks) {
            block.setType(material);
        }
    }
    public void clearDoorBoundBox(){  //清除屏障
        for (Block block : doorBoundBlocks) {
            block.setType(Material.AIR);
        }
    }
    /**
     * 控制门的动画，开启或者关闭
     */
    private void animateDoor(boolean opening) {

        double distance = moveDistance;
        new BukkitRunnable() {
            int step = 0;
            long totalSteps = doorTemplate.getAnimationTickTime();  //总时间/tick

            int num=0;
            @Override
            public void run() {
                if (step > totalSteps) {   //完成移动到最终为止,这里代表动画结束
                    completeAnimation(opening);
                    cancel();
                    return;
                }

                //门锁动画部分
                if(doorTemplate.isNeedAnimationButton()){
                    if (doorTemplate.getDoorLock() != null) {
                        DoorLock doorLock1 = doorTemplate.getDoorLock();
                        boolean overrideDoorLock = doorLock1 == DoorLock.NO_LEVEL_LOCK_LIGHT_OFF
                                || doorLock1 == DoorLock.NO_LEVEL_LOCK_LIGHT_ON;
                        long switchInterval = overrideDoorLock ? totalSteps / 3 : 3;
                        boolean shouldSwitch = (step % switchInterval == 0);

                        if (shouldSwitch) {
                            toggleButtons.forEach(button -> {
                                setDispaly(button, doorTemplate.getDoorLock().getAnimItemId(num));
                            });

                            num+=1;
                        }

                        if (step == totalSteps) {
                            new BukkitRunnable(){

                                @Override
                                public void run() {
                                    toggleButtons.forEach(button -> {
                                        setDispaly(button, doorTemplate.getDoorLock().endNum);
                                    });

                                    num=0;

                                    if (overrideDoorLock) {
                                        DoorLock doorLock = switch(doorTemplate.getDoorLock().id){
                                            case 40 -> DoorLock.NO_LEVEL_LOCK_LIGHT_OFF;
                                            case 41 -> DoorLock.NO_LEVEL_LOCK_LIGHT_ON;
                                            case 70 -> DoorLock.HEAVY_LOCK_BLUE;
                                            case 72 -> DoorLock.HEAVY_LOCK_GREEN;
                                            default -> DoorLock.NO_LEVEL_LOCK_LIGHT_ON;
                                        };
                                        doorTemplate.setDoorLock(doorLock);
                                    }
                                }
                            }.runTaskLater(SCPMain.getInstance(), 5l);


                        }
                    }
                }

                if(step == doorTemplate.getBoxDisappearTickTime() && opening){ //门屏障方块消失与显示时间
                    clearDoorBoundBox();
                }else if(step == doorTemplate.getBoxAppearTickTime() && !opening){
                    showDoorBoundBox();
                }

                double progress = (double) step / totalSteps;

                for (int i = 0; i < movingDisplays.size(); i++) {
                    BlockDisplay display = movingDisplays.get(i);
                    Location originalLoc = doorBoundBlocks.get(i).getLocation();

                    Location targetLoc;
                    if (opening) {
                        targetLoc = originalLoc.clone().add(moveDirection.clone().multiply(distance));
                    } else {
                        targetLoc = originalLoc.clone();
                    }

                    // 平滑插值移动
                    Location currentLoc = display.getLocation();
                    Location newLoc = interpolateLocation(currentLoc, targetLoc, progress);
                    display.teleport(newLoc);
                }

                for (int i = 0; i < icons.size(); i++) {
                    ItemDisplay display = icons.get(i);
                    Location originalLoc = iconsOriginLoc.get(i);

                    Location targetLoc;
                    if (opening) {
                        targetLoc = originalLoc.clone().add(moveDirection.clone().multiply(distance));
                    } else {
                        targetLoc = originalLoc.clone();
                    }

                    // 平滑插值移动
                    Location currentLoc = display.getLocation();
                    Location newLoc = interpolateLocation(currentLoc, targetLoc, progress);
                    display.teleport(newLoc);
                }

                step++;
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 1l);
    }
    /**
     * 平滑插值
     */
    private Location interpolateLocation(Location start, Location end, double progress) {
        double controlX = (start.getX() + end.getX()) / 2;
        double controlY = Math.max(start.getY(), end.getY());
        double controlZ = (start.getZ() + end.getZ()) / 2;

        double oneMinusT = 1 - progress;

        double x = oneMinusT * oneMinusT * start.getX() +
                2 * oneMinusT * progress * controlX +
                progress * progress * end.getX();

        double y = oneMinusT * oneMinusT * start.getY() +
                2 * oneMinusT * progress * controlY +
                progress * progress * end.getY();

        double z = oneMinusT * oneMinusT * start.getZ() +
                2 * oneMinusT * progress * controlZ +
                progress * progress * end.getZ();

        return new Location(start.getWorld(), x, y, z, start.getYaw(), start.getPitch());
    }
    /**
     * 完成动画后的状态转换
     */
    private void completeAnimation(boolean opening) {
        if (opening) {
            state = DoorState.OPEN;
        } else {
            state = DoorState.CLOSED;
            showDoorBoundBox();
        }
    }
    /**
     * 清除BlockDisplay实体
     */
    public void clearMovingDisplays() {
        for (BlockDisplay display : movingDisplays) {
            display.remove();
        }
        movingDisplays.clear();
    }

    /**
     * 清除视觉效果
     */
    public void clearVisual(){
        toggleButtons
                .stream()
                .forEach(lock -> {
            lock.remove();
        });

        icons.stream().forEach(icon->{
            icon.remove();
        });

        interactions.stream().forEach(interaction -> {
            interaction.remove();
        });

        Bukkit.getLogger().warning("视觉效果清理完毕");
    }
    /**
     * 设置连接门
     * @param door2
     * @param doorLinkType
     */
    public void setLinkEachOther(FloorContainmentDoor door2, DoorLinkType doorLinkType) {
        // 防止自连接
        Bukkit.getLogger().warning("door:"+this.id + "door2"+door2.getId());
        if (this.equals(door2)) {
            Bukkit.getLogger().warning("[DoorLink] 警告：尝试将门连接到自身: " + this.getId());
            return;
        }

        if(door2==null){
            Bukkit.getLogger().warning("[DoorLink]:链接的门为空" + door2.getId());
        }

        linkedDoorMap.put(door2, doorLinkType);
        door2.getLinkedDoorMap().put(this, doorLinkType);

        Bukkit.getLogger().info("[DoorLink] " + this.getId() + " ↔ " + door2.getId() + " (" + doorLinkType + ")");
    }
    public void handleLinkDoors(DoorState state){
        for (Map.Entry<FloorContainmentDoor, DoorLinkType> entry: linkedDoorMap.entrySet()) {
            FloorContainmentDoor key = entry.getKey();
            DoorLinkType value = entry.getValue();

            if(state==DoorState.OPEN){ //下一步是要关闭
                if(value==DoorLinkType.BOTH){
                    key.close();
                }else{
                    key.open();
                }

            }else if(state==DoorState.CLOSED){ //下一步是要开启
                if(value ==DoorLinkType.BOTH){
                    key.open();
                }else{
                    key.close();
                }
            }

        }
    }
    //<editor-fold desc="检测代码块">
    /**
     * 检测是否在碰撞盒内
     * @param location
     * @return
     */
    public boolean isInDoorBoundingBox(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double minX = collisionBox.getMinX()-0.2;
        double maxX = collisionBox.getMaxX()+0.2;

        double minY = collisionBox.getMinY()-0.2;
        double maxY = collisionBox.getMaxY()+0.2;

        double minZ = collisionBox.getMinZ()-0.2;
        double maxZ = collisionBox.getMaxZ()+0.2;
        return (x>= minX&& x<=maxX)
                && (y>= minY&& y<=maxY)
                && (z>= minZ&& z<=maxZ);
    }
    /**
     * 检测是否是大门的一部分
     * @param block
     * @return
     */
    public boolean isPartOfDoor(Block block) {
        return doorBoundBlocks.contains(block);
    }
    public ItemStack createDoorItemStack(){
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + id);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "位置: " + ChatColor.YELLOW + "[" + first.getBlockX() + "," + first.getBlockY() + "," + first.getBlockZ() + "]");
        lore.add(ChatColor.GRAY + "门包含的方块数量: " + ChatColor.YELLOW + doorBoundBlocks.size());
        lore.add(ChatColor.GRAY + "状态: " + ChatColor.YELLOW + state);
        lore.add(ChatColor.GRAY + "宽,高,深度: " + ChatColor.YELLOW + "[" + width + "," + height + "," + depth + "]");
        lore.add(ChatColor.GRAY + "门类型: " + ChatColor.YELLOW + doorTemplate.getId());
        lore.add(ChatColor.GRAY + "移动距离: " + ChatColor.YELLOW +moveDistance);

//        String d = null;
//        double x = Math.abs(moveDirection.getX());
//        double z = Math.abs(moveDirection.getZ());
//        if(x>z){
//            d = moveDirection.getX()>0? "+x" : "-x";
//        }else{
//            d = moveDirection.getZ()>0? "+z" : "-z";
//        }
//        lore.add(ChatColor.GRAY + "门的打开方向：" + ChatColor.YELLOW + d);


        lore.add(ChatColor.GRAY + "门的权限：" + ChatColor.YELLOW + "[" + doorTemplate.getPermissionsLevel().get(0)
                + "," + doorTemplate.getPermissionsLevel().get(1) + "," + doorTemplate.getPermissionsLevel().get(2) + "]");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    //</editor-fold>
    //<editor-fold desc="视觉效果代码块">
    /**
     *  在碰撞盒中心显示粒子效果,可视化，方便点击
     */
    public void displayCollisionBoxParticles(BoundingBox box) {
        World world = first.getWorld();

        int minX = (int) Math.floor(box.getMinX());
        int minY = (int) Math.floor(box.getMinY());
        int minZ = (int) Math.floor(box.getMinZ());
        int maxX = (int) Math.floor(box.getMaxX());
        int maxY = (int) Math.floor(box.getMaxY());
        int maxZ = (int) Math.floor(box.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location particleLoc = new Location(world, x, y, z);
                    try {
                        world.spawnParticle(Particle.LAVA, particleLoc, 1,
                                0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(0, 0, 255), 1));
                    } catch (Exception e) {
                        world.spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 1,
                                0, 0, 0, 0);
                    }
                }
            }
        }
    }
    /**
     * 创建一个textdisplay文本
     * @return
     */
    public TextDisplay createTextDisplay(){
        TextDisplay textDisplay = first.getWorld().spawn(first.clone().add(0,2,0), TextDisplay.class);
        textDisplay.setText("§f§l" + id);
        textDisplay.setSeeThrough(true);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(150, 0, 0, 0));
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setTextOpacity((byte) 255);

        return textDisplay;
    }
    public void removeTextDisplay(){
        if(textDisplay==null){
            return;
        }

        textDisplay.remove();
        this.textDisplay = null;
    }
    //</editor-fold>
    //<editor-fold desc="序列化，反序列化，暂时遗弃">
    // 序列化为 Map
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", this.id);
        data.put("origin", this.first);
        data.put("width", this.width);
        data.put("height", this.height);
        data.put("depth", this.depth);
        data.put("distance", this.moveDistance);
        data.put("DoorTemplateId", this.doorTemplate.getId());
        data.put("moveDirection", this.moveDirection);
//        data.put("linkdoor", this.linkDoor != null ? this.linkDoor.getId() : null);
//        data.put("DoorLinkType", this.DoorLinkType != null ? this.DoorLinkType.name() : null);
        return data;
    }
    // 从 Map 反序列化
    public static FloorContainmentDoor deserialize(Map<String, Object> data, DoorManager doorManager) {
//        String id = (String) data.get("name");
//        Location origin = (Location) data.get("origin");
//        int width = (int) data.get("width");
//        int height = (int) data.get("height");
//        int depth = (int) data.get("depth");
//        double distance = (double) data.get("distance");
//        String DoorTemplateId = (String) data.get("DoorTemplateId");
//        Vector direction = (Vector) data.get("direction");
//
//        String DoorLinkTypeStr = (String) data.get("DoorLinkType");
//        DoorLinkType DoorLinkType = null;
//        if(DoorLinkTypeStr != null){
//            try{
//                DoorLinkType = DoorLinkType.valueOf(DoorLinkTypeStr.toUpperCase());
//            }catch (IllegalArgumentException e){
//                Bukkit.getLogger().warning("无效的链接类型: " + DoorLinkTypeStr);
//            }
//        }
//
//        String linkDoorStr = (String) data.get("linkdoor");
//        FloorContainmentDoor linkDoor = doorManager.idGetDoor(linkDoorStr);
//
//        DoorTemplate DoorTemplate = doorManager.getDoorTemplate(DoorTemplateId);
//        if (DoorTemplate == null) {
//            Bukkit.getLogger().warning("无法找到门类型: " + DoorTemplateId + "，门 " + id + " 加载失败");
//            return null;
//        }
//
//        FloorContainmentDoor Door = new FloorContainmentDoor(id, origin,  ,width, height, depth, distance, DoorTemplate, direction);
//
//        //如果linkDoor为null，就不进行链接
//        if(linkDoor!=null && DoorLinkType!=DoorLinkType.NONE){
//            Door.setLinkEachOther(linkDoor, DoorLinkType);
//            linkDoor.setLinkEachOther(Door, DoorLinkType);
//        }else{
//            Bukkit.getLogger().info("[DoorManager]: door link fail");
//        }
//
//        return Door;
        return null;
    }
    //</editor-fold>
    @Override
    public String toString() {
        return "Door{" +
                "name='" + id + '\'' +
                ", state=" + state +
                ", width=" + width +
                ", height=" + height +
                ", depth=" + depth +
                ", moveDistance=" + moveDistance +
                ", direction=" + moveDirection +
                '}';
    }
}