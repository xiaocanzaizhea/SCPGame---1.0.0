package xyz.xiaocan.scpitemstacks.card;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.itemoption.card.Card;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.card.snake.*;
import xyz.xiaocan.tools.util;

import java.util.Queue;
import java.util.Random;
import java.util.UUID;


public class Decoder extends AbstractSCPItem
        implements IOnRightClick {

    private static final String INVENTORY_TITLE = "§2§o§n§lSnake";
    private static final int INVENTORY_SIZE = 54;

    private static ItemStack food = Food.getInstance().getFood();
    private Snake snake;
    private static long speed = 20;
    private int currentScore;
    private BukkitTask snakeTask;
    private Listener inventoryListener;
    public Decoder(Card card) {
        super(card.getId(), card.getDisPlayName(),
                card.getMaterial(), card.getCustomModelData(), null);
    }
    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Inventory snakeInventory = Bukkit.createInventory(null, INVENTORY_SIZE, INVENTORY_TITLE);

        //------------初始化-----------
        initInventory(snakeInventory);
        initPlayerInventory(player, true);
        currentScore = 0;
        //----------------------------

        player.openInventory(snakeInventory);

        final UUID playerId = player.getUniqueId();
        final Inventory inventory = snakeInventory;

        inventoryListener = registerListener(event.getPlayer());  //监听玩家点击移动按钮
        snakeTask = new BukkitRunnable() {
            @Override
            public void run() {
                Player taskPlayer = Bukkit.getPlayer(playerId);
                if (taskPlayer == null || !taskPlayer.isOnline()) {
                    gameOver(inventory,player);
                    cleanup(player);
                    return;
                }

                Inventory openInv = taskPlayer.getOpenInventory().getTopInventory();
                if (!openInv.equals(inventory)) {
                    gameOver(inventory, player);
                    cleanup(player);
                    return;
                }

                snakeMove(inventory, player);
            }


        }.runTaskTimer(SCPMain.getInstance(), 0L, speed);
    }
    private void initInventory(Inventory inventory){
        //生成蛇
        int snakeCoord = getRandomSafePlace(inventory);
        snake = new Snake(snakeCoord, Key.LEFT);
        inventory.setItem(snake.getHead().getSlot(), snake.getHead().getPart());

        //生成食物
        spawnNewFood(inventory);
    }
    private void initPlayerInventory(Player player, boolean isOpen){
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(22, isOpen?Key.UP.getItem(): util.background);
        inventory.setItem(30, isOpen?Key.LEFT.getItem(): util.background);
        inventory.setItem(31, isOpen?Key.DOWN.getItem(): util.background);
        inventory.setItem(32, isOpen?Key.RIGHT.getItem(): util.background);

        ItemStack item = RankBoard.getInstance().createItem();
        inventory.setItem(26, isOpen?item: util.background);
    }
    private void snakeMove(Inventory inventory, Player player){
        int headOldSlot = snake.getHead().getSlot();
        Key key = snake.getMoveDirection();
        int headNewSlot = 0;

        int row = headOldSlot / 9;
        int col = headOldSlot % 9;
        if(key==Key.UP){ //处理边界情况

            row-=1;
            if(row<0){ //超出边界
                row = 5;
            }
        }else if(key==Key.DOWN){

            row+=1;
            if(row>5){
                row=0;
            }
        }else if(key==Key.LEFT){

            col-=1;
            if(col<0){
                col=8;
            }
        }else if(key==Key.RIGHT){

            col+=1;
            if(col>8){
                col=0;
            }
        }

        headNewSlot = row * 9 + col;
        ItemStack newPlaceItem = inventory.getItem(headNewSlot);

        Queue<ItemInInventory> tails = snake.getBody(); //存储着所有尾巴
        if(newPlaceItem==null){

            ItemInInventory poll = tails.poll();  //设置原来的位置为空
            inventory.setItem(poll.getSlot(), null);

            inventory.setItem(headOldSlot,poll.getPart()); //设置头走过的位置为尾巴

            tails.add(new ItemInInventory(poll.getPart(), headOldSlot)); //新增一个尾巴进队列

        } else if(newPlaceItem.equals(food)){ //为食物,不移动蛇尾，新增一个

            currentScore +=1;

            ItemInInventory newTail = new ItemInInventory(snake.getTailItem(), headOldSlot);
            inventory.setItem(headOldSlot,snake.getTailItem());

            tails.add(newTail);  //新增一个

            spawnNewFood(inventory);
            changeSpeed();

        }else if(newPlaceItem.equals(snake.getTailItem())){ //为蛇尾
            Bukkit.getLogger().warning("游戏结束");
            gameOver(inventory, player);
            return;
        }

        snake.getHead().setSlot(headNewSlot);                                   //更新头的slot
        inventory.setItem(headNewSlot,snake.getHead().getPart());    //设置新位置
    }

    private void changeSpeed() {
        if(speed<=20){
            speed = 20;
        }else if(speed<=40){
            speed = 15;
        }else{
            speed = 10;
        }
    }

    private void gameOver(Inventory inventory, Player player) {
        if (snakeTask != null && !snakeTask.isCancelled()) {
            snakeTask.cancel();
            snakeTask = null;
        }

        if (player.getOpenInventory().getTopInventory().equals(inventory)) {
            player.closeInventory();
        }

        int score = currentScore;
        player.sendMessage(ChatColor.RED + "游戏结束！你的得分: " + score);
        RankBoard.getInstance().addScore(player.getName(), score);
        initPlayerInventory(player,false);

        cleanup(player);
    }
    private void spawnNewFood(Inventory inventory){
        int foodCoord = getRandomSafePlace(inventory);
        inventory.setItem(foodCoord,food);
    }
    private int getRandomSafePlace(Inventory inventory){
        Random random = new Random();
        int slot = random.nextInt(INVENTORY_SIZE - 1);

        if(inventory.getItem(slot)!=null){
            return getRandomSafePlace(inventory);
        }

        return slot;
    }
    private void cleanup(Player player) {
        cancelListen(player, inventoryListener);
        cancelTask(player,snakeTask);
    }
    private Listener registerListener(Player player) {
        Listener snakeListener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player clicker)) return;
                if (!clicker.getUniqueId().equals(player.getUniqueId())) return;

                // 2. 只取消玩家背包的点击（不影响蛇游戏界面）
                if (event.getClickedInventory() != null &&
                        event.getClickedInventory().getType() == InventoryType.PLAYER) {
                    event.setCancelled(true); // 只取消玩家背包的点击

                    clicker.playSound(clicker.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

                    ItemStack currentItem = event.getCurrentItem();
                    if (currentItem == null) return;

                    if (Key.UP.getItem().isSimilar(currentItem)) {
                        snake.setMoveDirection(Key.UP);
                    } else if (Key.DOWN.getItem().isSimilar(currentItem)) {
                        snake.setMoveDirection(Key.DOWN);
                    } else if (Key.LEFT.getItem().isSimilar(currentItem)) {
                        snake.setMoveDirection(Key.LEFT);
                    } else if (Key.RIGHT.getItem().isSimilar(currentItem)) {
                        snake.setMoveDirection(Key.RIGHT);
                    }
                }
            }
        };

        Bukkit.getPluginManager()
                .registerEvents(snakeListener, SCPMain.getInstance());

        player.setMetadata("snake_listener",
                new FixedMetadataValue(SCPMain.getInstance(), snakeListener));
        return snakeListener;
    }
    private void cancelListen(Player player, Listener listener) {
        HandlerList.unregisterAll(listener);
        player.removeMetadata("snake_listener", SCPMain.getInstance());
    }
}
