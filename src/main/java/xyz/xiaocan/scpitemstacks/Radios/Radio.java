package xyz.xiaocan.scpitemstacks.Radios;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.chatsystem.ChatChannel;
import xyz.xiaocan.chatsystem.DistanceChatManager;
import xyz.xiaocan.configload.option.itemoption.RadiosSetting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.*;
import xyz.xiaocan.tools.progressBar;

public class Radio extends AbstractSCPItem
        implements IOnRightClick, IOnLeftClick, IOnSwitchItemBar, IOnPlayerDrop {
    public RadiosSetting radio;
    public boolean isTurnOn;
    public int stateNum;
    public float currentPower;
    public BukkitTask visualPowerTask;
    public Radio(RadiosSetting radio) {
        super(radio.id, radio.displayName,
                radio.material, radio.customModelData, radio.lore);

        this.radio = radio;
        this.isTurnOn = false;
        this.stateNum = 1;
        this.currentPower = radio.totalPower;
    }

    @Override
    public void OnLeftClick(PlayerInteractEvent event) {//changestate
        if(isTurnOn){
            this.stateNum = (stateNum + 1) % RadiosStates.values().length;

            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
        } else {
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 0.8f, 0.8f);
        }

        updatePlayerChatChannel(event.getPlayer());
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        this.isTurnOn = !this.isTurnOn;

        if (isTurnOn) {
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.2f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.8f, 1.8f);
            createTask(player);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0f, 0.8f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 0.5f, 0.5f);
            cancelTask(player,visualPowerTask);
        }

        updatePlayerChatChannel(player);
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        if(check){ //过来
            if(isTurnOn){
                createTask(event.getPlayer());
            }
        }else{ //切走
            cancelTask(event.getPlayer(), visualPowerTask);
            DistanceChatManager.setPlayerChatMode(event.getPlayer(), ChatChannel.LOCAL);
        }
    }

    public void createTask(Player player){
        if(visualPowerTask!=null && !visualPowerTask.isCancelled())return;

        visualPowerTask = new BukkitRunnable(){
            RadiosStates[] values = RadiosStates.values();
            @Override
            public void run() {

                progressBar.updateUseProgress(player,
                        currentPower,radio.totalPower,"对讲机剩余电量[" + values[stateNum] + "]");
            }
        }.runTaskTimer(SCPMain.getInstance(),0l,20l);
    }

    @Override
    public void onPlayerDrop(PlayerDropItemEvent event) {
        cancelTask(event.getPlayer(),visualPowerTask);
    }

    private void updatePlayerChatChannel(Player player){
        if(isTurnOn){
            switch (stateNum){
                case 0:
                    DistanceChatManager.playerModes.put(player.getUniqueId(),ChatChannel.RADIO_SR);
                    break;
                case 1:
                    DistanceChatManager.playerModes.put(player.getUniqueId(),ChatChannel.RADIO_MR);
                    break;
                case 2:
                    DistanceChatManager.playerModes.put(player.getUniqueId(),ChatChannel.RADIO_LR);
                    break;
                case 3:
                    DistanceChatManager.playerModes.put(player.getUniqueId(),ChatChannel.RADIO_UR);
                    break;
            }
        }else{//关闭
            DistanceChatManager.playerModes.put(player.getUniqueId(), ChatChannel.LOCAL);
        }
    }

    public void consumePower(Player player, boolean isSender, int length){ //2种消耗电力的方式，一种是主动发，消耗更大，一种是被动接受，消耗更少
        RadiosStates state = RadiosStates.values()[stateNum];
        if(isSender){
            currentPower = Math.max(0, currentPower -
                    state.usePower * radio.eachCharacterConsumes * length);
        }else{
            currentPower = Math.max(0, currentPower -
                    state.waitPower * radio.eachCharacterConsumes * length);
        }

        if(currentPower<=0)DistanceChatManager
                .setPlayerChatMode(player,ChatChannel.LOCAL);
    }

}
