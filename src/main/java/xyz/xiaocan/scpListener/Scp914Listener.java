package xyz.xiaocan.scpListener;

import org.bukkit.*;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.configload.option.scpoption.SCP914Setting;
import xyz.xiaocan.dropitemsystem.DropItem;
import xyz.xiaocan.dropitemsystem.DropManager;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.card.CardType;
import xyz.xiaocan.scpitemstacks.terrainSCP.scp914.SCP914;
import xyz.xiaocan.scpitemstacks.terrainSCP.scp914.SCP914State;

import java.util.*;

public class Scp914Listener implements Listener {

    @EventHandler
    public void onPlayerInteractModel(PlayerInteractAtEntityEvent event) {

        if (!(event.getRightClicked() instanceof Interaction)) {
            return;
        }

        Interaction interaction = (Interaction) event.getRightClicked();
        String is914 = interaction.
                getPersistentDataContainer().
                get(SCP914.scp914, PersistentDataType.STRING);

        if(is914==null || !is914.equals("model")){
            return;
        }

        SCP914 scp914 = SCP914.getInstance();

        if (scp914.getCurrentState() != SCP914State.Waiting) {
            return;
        }

        scp914.changeModel();
    }

    @EventHandler
    public void onPlayerInteractStart(PlayerInteractAtEntityEvent event) {

        if (!(event.getRightClicked() instanceof Interaction)) {
            return;
        }

        Interaction interaction = (Interaction) event.getRightClicked();
        String is914 = interaction.
                getPersistentDataContainer().
                get(SCP914.scp914, PersistentDataType.STRING);

        if(is914==null || !is914.equals("start")){
            return;
        }

        SCP914 scp914 = SCP914.getInstance();
        SCP914Setting scp914Setting = SCP914Setting.getInstance();

        if (scp914.getCurrentState() != SCP914State.Waiting) {
            return;
        }

        scp914.changeSCP914State(SCP914State.Processing);

        // 关门开始执行逻辑
        new BukkitRunnable() {
            @Override
            public void run() {
                scp914.toggleSCP914Door();

                new BukkitRunnable(){
                    int t = 0;
                    long handleTime = scp914Setting.getHandleTime();
                    @Override
                    public void run() {
                        Location colMidLcation = scp914.getColMidLcation();
                        Location colMidLcation1 = scp914.getColMidLcation1();
                        colMidLcation.
                                getWorld().
                                playSound(
                                        colMidLcation, Sound.BLOCK_NOTE_BLOCK_BELL,0.3f,0.3f);  //持续播放加工中的声音

                        spawnCustomPartical(colMidLcation, Particle.CAMPFIRE_COSY_SMOKE);
                        spawnCustomPartical(colMidLcation, Particle.SMOKE);

                        spawnCustomPartical(colMidLcation1,Particle.CAMPFIRE_COSY_SMOKE);
                        spawnCustomPartical(colMidLcation1,Particle.SMOKE);

                        t+=20;
                        if(t>=handleTime){ //结束逻辑
                            handlePlayerTeleportAndItemConversion();
                            scp914.changeSCP914State(SCP914State.Waiting);
                            scp914.toggleSCP914Door();

                            this.cancel();
                        }
                    }
                }.runTaskTimer(SCPMain.getInstance(),0l,20l);
            }
        }.runTaskLater(SCPMain.getInstance(), scp914Setting.getStartTime());
    }

    public void spawnCustomPartical(Location location, Particle particle){
        int count = 10;
        double offsetX = 0.2;
        double offsetZ = 0.2;
        double offsetY = 0.1;
        double speed = 0.02;

        location.getWorld().spawnParticle(
                particle,
                location,
                count,
                offsetX, offsetY, offsetZ,
                speed
        );
    }

    public void handlePlayerTeleportAndItemConversion(){ //1
        BoundingBox boundingBox = SCP914.getInstance().getBoundingBox();
        Map<CardType, Card> allCards = ItemManager.getInstance().getAllCards();
        DropManager dropManager = DropManager.getInstance();
        Map<UUID, DropItem> dropItemMap = dropManager.getDropItemMap();
        SCP914 scp914 = SCP914.getInstance();
        int modelNum = scp914.getScp914Model().num;

        for (Player player:Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();
            if(!boundingBox.contains(playerLocation.toVector())){
                continue;
            }

            Location mappingLocation = getMappingLocation(playerLocation);
            player.teleport(mappingLocation);

            //处理玩家主手拿的物品
            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            String cardId = Card.getCardId(itemInMainHand);
            if(cardId==null){
                continue;
            }

            CardType cardType = CardType.valueOf(cardId);
            String randomCardOfList = SCP914Setting.getInstance().getRandomCardOfList(cardType, modelNum);
            if(randomCardOfList!=null){
                CardType traget = CardType.valueOf(randomCardOfList);
                Card card = allCards.get(traget);
                player.getInventory().setItemInMainHand(card.createCardItemStack());
            }else{
                player.getInventory().setItemInMainHand(null);
            }

        }

        for (DropItem dropItem:dropItemMap.values()) {
            Location location = dropItem.getLocation();

            if(!boundingBox.contains(location.toVector())){
                continue;
            }

            String cardId = Card.getCardId(dropItem.getItemStack());
            if(cardId==null){
                continue;
            }

            CardType cardType = CardType.valueOf(cardId);
            String randomCardOfList =   //从转化列表随机出的一张卡
                SCP914Setting.
                        getInstance().
                        getRandomCardOfList(cardType, modelNum);

            if(randomCardOfList!=null){
                CardType converCardType = CardType.valueOf(randomCardOfList);
                Card card = allCards.get(converCardType);

                ItemStack cardItemStack = card.createCardItemStack();
                dropItem.setItemStack(cardItemStack);
                dropItem.getItemDisplay().setItemStack(cardItemStack);
                dropItem.getInteraction().setCustomName(cardItemStack.getItemMeta().getDisplayName());

                Location mappingLocation = getMappingLocation(location);
                dropItem.teleport(mappingLocation);
            }else{
                dropItem.remove();
                dropItem=null;
            }
        }
    }

    private Location getMappingLocation(Location location) {
        BoundingBox box = SCP914.getInstance().getBoundingBox();
        BoundingBox box1 = SCP914.getInstance().getBoundingBox1();

        double newX = box1.getMinX() + (location.getX() - box.getMinX());
        double newY = box1.getMinY() + (location.getY() - box.getMinY());
        double newZ = box1.getMinZ() + (location.getZ() - box.getMinZ());

        Location newLoc =
                new Location(location.getWorld(),
                        newX, newY, newZ, location.getYaw(),location.getPitch());
        return newLoc;
    }
}
