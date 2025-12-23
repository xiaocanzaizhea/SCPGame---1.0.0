package xyz.xiaocan.configload.option;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.doorsystem.DoorLock;
import xyz.xiaocan.doorsystem.DoorMoveMode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
/*
    处理从配置文件中读取到的数据
 */
public class DoorTemplate{
    private String id;
    private Material material;
    private boolean canBreak;
    private boolean canOpenByPlayer;
    private double itemDisplaySize;
    private List<Integer> permissionsLevel; //三类权限需要的最低等级

    private boolean needAnimation;  //门的动画
        private Sound openSoundEffect;
        private Sound closedSoundEffect;
        private Sound failSoundEffect;

        private long animationTickTime;
        private long boxDisappearTickTime;
        private long boxAppearTickTime;

        private double autoClosed;
        private DoorMoveMode doorMoveMode;
        private boolean needAnimationButton; //门锁动画
        private boolean isElevator;
        private boolean needIcon;
            private DoorLock doorLock;


    public DoorTemplate(String id, List<Integer> permissionsLevel, String material,
                        double ItemDisplaySize, boolean canBreak, boolean canOpenByPlayer,
                        boolean needAnimation,
                        String openSoundEffect, String closedSoundEffect,
                        String failSoundEffect, double animationTickTime, double boxDisappearTickTime,
                        double boxAppearTickTime, double autoClosed, String doorMoveMode,
                        boolean needAnimationButton, boolean needIcon, String doorLock){
        this.id = id;
        this.permissionsLevel = permissionsLevel;

        try{
            this.material = Material.valueOf(material);
        }catch (Exception e){
            Bukkit.getLogger().warning("[DoorTemplate]: material配置错误, id:" + id);
        }

        this.canBreak = canBreak;
        this.canOpenByPlayer = canOpenByPlayer;
        this.itemDisplaySize = ItemDisplaySize;

        this.needAnimation = needAnimation;
        this.openSoundEffect = Sound.valueOf(openSoundEffect);
        this.closedSoundEffect = Sound.valueOf(closedSoundEffect);
        this.failSoundEffect = Sound.valueOf(failSoundEffect);
        this.animationTickTime = (long)(animationTickTime*20);
        this.boxDisappearTickTime = (long)(boxDisappearTickTime*20);
        this.boxAppearTickTime = (long)(boxAppearTickTime*20);
        this.autoClosed = autoClosed;
        this.doorMoveMode = DoorMoveMode.getMoveMode(doorMoveMode);
        this.needAnimationButton = needAnimationButton;
        this.needIcon = needIcon;

        try{
            if(!doorLock.equals("[DESTORY]")){
                this.doorLock = DoorLock.valueOf(doorLock);
            }else{
                this.doorLock = null;
            }
        }catch (Exception e){
            Bukkit.getLogger().warning("门锁配置错误");
        }
    }

    public ItemStack createDoorItemStack(){
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + id);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "gate permission: " + ChatColor.YELLOW + permissionsLevel.get(0));
        lore.add(ChatColor.GRAY + "door permission: " + ChatColor.YELLOW + permissionsLevel.get(1));
        lore.add(ChatColor.GRAY + "weapon permission: " + ChatColor.YELLOW + permissionsLevel.get(2));
        lore.add(ChatColor.GRAY + "OpenSoundEffect: " + ChatColor.YELLOW + openSoundEffect);
        lore.add(ChatColor.GRAY + "ClosedSoundEffect: " + ChatColor.YELLOW + closedSoundEffect);
        lore.add(ChatColor.GRAY + "FailSoundEffect: " + ChatColor.YELLOW + failSoundEffect);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public String toString() {
        return "DoorTemplate{" +
                "id='" + id + '\'' +
                ", openSoundEffect=" + openSoundEffect +
                ", closedSoundEffect=" + closedSoundEffect +
                ", failSoundEffect=" + failSoundEffect +
                ", permissionsLevel=" + permissionsLevel +
                ", animationTickTime=" + animationTickTime +
                ", boxDisappearTickTime=" + boxDisappearTickTime +
                ", boxAppearTickTime=" + boxAppearTickTime +
                '}';
    }
}
