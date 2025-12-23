package xyz.xiaocan.scpitemstacks.card.snake;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xiaocan.scpgame.SCPMain;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class RankBoard {
    private static RankBoard instance;

    private PriorityQueue<Integer> scoreQueue;
    private Map<Integer, String> scoreToPlayer;
    private static final int MAX_RANK = 10;

    private File configFile;
    private FileConfiguration config;
    private RankBoard() {
        scoreQueue = new PriorityQueue<>(Collections.reverseOrder());
        scoreToPlayer = new HashMap<>();

        // 初始化配置文件
        setupConfig();
        // 加载数据
        loadData();
    }
    private void setupConfig() {
        // 确保插件数据文件夹存在
        File dataFolder = SCPMain.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // 创建snake子文件夹
        File snakeFolder = new File(dataFolder, "snake");
        if (!snakeFolder.exists()) {
            snakeFolder.mkdirs();
        }

        // 创建配置文件
        configFile = new File(snakeFolder, "rankboard.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Bukkit.getLogger().info("创建排行榜配置文件: " + configFile.getPath());
            } catch (IOException e) {
                Bukkit.getLogger().severe("无法创建排行榜配置文件: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveData() {
        try {
            // 清空现有数据
            config.set("rankboard", null);

            // 保存当前排行榜数据
            List<Integer> sortedScores = new ArrayList<>(scoreQueue);
            sortedScores.sort(Collections.reverseOrder());

            int rank = 1;
            for (Integer score : sortedScores) {
                String playerName = scoreToPlayer.get(score);
                if (playerName == null) continue;

                String key = "rankboard.rank" + rank;
                config.set(key + ".player", playerName);
                config.set(key + ".score", score);

                rank++;
                if (rank > MAX_RANK) break;
            }

            // 保存到文件
            config.save(configFile);
            Bukkit.getLogger().info("排行榜数据已保存");

        } catch (IOException e) {
            Bukkit.getLogger().severe("保存排行榜数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadData() {
        try {
            // 检查配置是否存在
            if (!configFile.exists()) {
                Bukkit.getLogger().info("排行榜配置文件不存在，使用空数据");
                return;
            }

            // 重新加载配置（避免缓存）
            config = YamlConfiguration.loadConfiguration(configFile);

            // 清空现有数据
            scoreQueue.clear();
            scoreToPlayer.clear();

            // 加载数据
            for (int rank = 1; rank <= MAX_RANK; rank++) {
                String key = "rankboard.rank" + rank;

                String playerName = config.getString(key + ".player");
                Integer score = config.getInt(key + ".score");

                if (playerName != null && score > 0) {
                    scoreQueue.offer(score);
                    scoreToPlayer.put(score, playerName);
                    Bukkit.getLogger().info("加载排名 " + rank + ": " + playerName + " - " + score + "分");
                }
            }

            Bukkit.getLogger().info("排行榜数据加载完成，共 " + scoreQueue.size() + " 条记录");

        } catch (Exception e) {
            Bukkit.getLogger().severe("加载排行榜数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static RankBoard getInstance() {
        if (instance == null) {
            instance = new RankBoard();
        }
        return instance;
    }
    public void addScore(String playerName, int score) {
        if (score <= 0) return;

        // 如果分数已存在，跳过
        if (scoreToPlayer.containsKey(score)) return;

        if (scoreQueue.size() < MAX_RANK) {
            scoreQueue.offer(score);
            scoreToPlayer.put(score, playerName);
        } else {
            Integer minScore = scoreQueue.peek();
            if (minScore != null && score > minScore) {
                Integer removedScore = scoreQueue.poll();
                if (removedScore != null) {
                    scoreToPlayer.remove(removedScore);
                }

                scoreQueue.offer(score);
                scoreToPlayer.put(score, playerName);
            }
        }

        // 保存数据
        saveData();
    }
    public ItemStack createItem() {
        ItemStack paper = new ItemStack(Material.EMERALD);
        ItemMeta meta = paper.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "贪吃蛇排行榜");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "========================");

        if (scoreQueue.isEmpty()) {
            lore.add(ChatColor.RED + "暂无游戏记录");
            lore.add(ChatColor.GRAY + "快去玩一局吧！");
        } else {
            List<Integer> sortedScores = new ArrayList<>(scoreQueue);
            sortedScores.sort(Collections.reverseOrder());

            int rank = 1;
            for (Integer score : sortedScores) {
                String playerName = scoreToPlayer.get(score);
                if (playerName == null) continue;

                String rankIcon = getRankIcon(rank);

                lore.add(rankIcon + ChatColor.WHITE + playerName +
                        ChatColor.GRAY + " - " +
                        ChatColor.GREEN + score + "分");

                rank++;
            }
        }

        lore.add(ChatColor.GRAY + "========================");
        lore.add(ChatColor.DARK_GRAY + "最多显示前10名记录");
        lore.add(ChatColor.DARK_GRAY + "更新时间: " + getCurrentTime());

        meta.setLore(lore);
        paper.setItemMeta(meta);

        return paper;
    }
    private String getRankIcon(int rank) {
        switch (rank) {
            case 1: return ChatColor.GOLD + "❶ ";
            case 2: return ChatColor.GRAY + "❷ ";
            case 3: return ChatColor.YELLOW + "❸ ";
            default: return ChatColor.WHITE + " " + rank + ". ";
        }
    }
    private String getCurrentTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
    }
    public void onDisable() {
        saveData();
    }
}