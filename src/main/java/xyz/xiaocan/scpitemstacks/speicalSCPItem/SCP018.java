package xyz.xiaocan.scpitemstacks.speicalSCPItem;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP018Setting;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.AbstractSCPItem;
import xyz.xiaocan.scpitemstacks.IOnRightClick;
import xyz.xiaocan.scpitemstacks.IOnSwitchItemBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//暂时不管
//todo
public class SCP018 extends AbstractSCPItem implements IOnRightClick, IOnSwitchItemBar, Listener {

    //todo 待改
    public SCP018Setting scp018;
    public static Map<UUID, Long> activationTime = new HashMap<>(); // 记录激活时间
    public static Map<UUID, Boolean> isActive = new HashMap<>(); // 记录是否激活

    public SCP018(SCP018Setting scp018) {
        super(scp018.id, scp018.disPlayName,
                scp018.material, scp018.customModelData, scp018.lore);

        this.scp018 = scp018;
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, SCPMain.getInstance());
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 扔出SCP-018
        throwSCP018(player, item);
    }

    private boolean hasRequiredPermissions(Player player) {
        // 这里需要根据你的权限系统实现具体的权限检查
        // 示例：检查二级收容权限和检查点权限
        return player.hasPermission("scp.containment.level2") &&
                player.hasPermission("scp.checkpoint");
    }

    private void throwSCP018(Player player, ItemStack item) {
        // 从玩家手中移除物品
        player.getInventory().setItemInMainHand(null);

        // 创建雪球实体作为SCP-018的载体
        Snowball ball = player.launchProjectile(Snowball.class);
        ball.setItem(item);
        ball.setShooter(player);

        // 设置元数据标识这是SCP-018
        ball.setMetadata("SCP018", new FixedMetadataValue(SCPMain.getInstance(), true));
        ball.setMetadata("SCP018_Owner", new FixedMetadataValue(SCPMain.getInstance(), player.getUniqueId().toString()));
        ball.setMetadata("SCP018_ActivationTime", new FixedMetadataValue(SCPMain.getInstance(), System.currentTimeMillis()));

        // 记录激活信息
        UUID ballId = ball.getUniqueId();
        activationTime.put(ballId, System.currentTimeMillis());
        isActive.put(ballId, true);

        // 播放投掷音效
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.0f);

        // 启动定时任务检查爆炸
        startExplosionTimer(ball);
    }

    private void startExplosionTimer(Snowball ball) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!ball.isValid() || !isActive.getOrDefault(ball.getUniqueId(), false)) {
                    cancel();
                    return;
                }

                long currentTime = System.currentTimeMillis();
                long activationTime = SCP018.activationTime.getOrDefault(ball.getUniqueId(), currentTime);
                long elapsedTime = (currentTime - activationTime) / 1000;

                // 80秒后爆炸
                if (elapsedTime >= scp018.moveTime) {
                    explode(ball);
                    cancel();
                }
            }
        }.runTaskTimer(SCPMain.getInstance(), 0L, 20L); // 每秒检查一次
    }

    private void explode(Snowball ball) {
        Location location = ball.getLocation();
        World world = location.getWorld();

        // 播放爆炸效果
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 10);

        // 造成爆炸伤害
        for (Entity entity : world.getNearbyEntities(location, 3, 3, 3)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                // 计算爆炸伤害
                double distance = livingEntity.getLocation().distance(location);
                double damage = scp018.explosionDamage * (1 - distance / 3); // 距离衰减

                if (damage > 0) {
                    livingEntity.damage(damage);
                }
            }
        }

        // 清理数据
        UUID ballId = ball.getUniqueId();
        activationTime.remove(ballId);
        isActive.remove(ballId);
        ball.remove();
    }

    // 处理碰撞事件
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball)) return;

        Snowball ball = (Snowball) event.getDamager();
        if (!ball.hasMetadata("SCP018")) return;

        // 这是SCP-018造成的伤害
        event.setCancelled(true); // 取消原版伤害，使用自定义伤害计算

        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getEntity();
            applySCP018Damage(ball, target);
        }
    }

    private void applySCP018Damage(Snowball ball, LivingEntity target) {
        // 计算速度相关的伤害
        double speed = ball.getVelocity().length();
        double baseDamage = speed * scp018.baseDamage;

        // 应用伤害乘数
        double finalDamage = baseDamage;
        if (target instanceof Player) {
            // 对玩家应用可能的阵营伤害逻辑
            finalDamage = applyFactionDamage(ball, target, finalDamage);
        } else {
            // 对SCP应用10倍伤害
            finalDamage *= scp018.damageMultiplierSCP;
        }

        if (finalDamage > 0) {
            target.damage(finalDamage);
        }

        // 破坏玻璃和门
        breakBlocks(ball);

        // 弹跳效果
        bounce(ball);
    }

    private double applyFactionDamage(Snowball ball, LivingEntity target, double damage) {
        // 这里需要根据你的阵营系统实现伤害逻辑
        // 示例：10秒缓冲期内只伤害敌对阵营，之后伤害所有阵营

        long currentTime = System.currentTimeMillis();
        long activationTime = ball.getMetadata("SCP018_ActivationTime").get(0).asLong();
        long elapsedTime = (currentTime - activationTime) / 1000;

        if (elapsedTime <= scp018.bufferTime) {
            // 缓冲期内，只伤害敌对阵营和自己
            // 需要根据你的阵营系统判断是否为敌对
            if (isHostile(ball, target) || isOwner(ball, target)) {
                return damage;
            } else {
                return 0; // 不伤害队友
            }
        } else {
            // 缓冲期后，伤害所有阵营
            return damage;
        }
    }

    private boolean isHostile(Snowball ball, LivingEntity target) {
        // 根据你的阵营系统判断是否为敌对
        // 这里需要你根据实际系统实现
        return true; // 示例
    }

    private boolean isOwner(Snowball ball, LivingEntity target) {
        if (!(target instanceof Player)) return false;
        String ownerUUID = ball.getMetadata("SCP018_Owner").get(0).asString();
        return target.getUniqueId().toString().equals(ownerUUID);
    }

    private void breakBlocks(Snowball ball) {
        Location location = ball.getLocation();
        Material blockType = location.getBlock().getType();

        // 破坏玻璃
        if (isGlass(blockType)) {
            location.getBlock().breakNaturally();
        }

        // 破坏普通门（非重型闸门和检查点门）
        if (isNormalDoor(blockType)) {
            location.getBlock().breakNaturally();
        }
    }

    private boolean isGlass(Material material) {
        return material.name().contains("GLASS");
    }

    private boolean isNormalDoor(Material material) {
        return material.name().contains("DOOR") &&
                !material.name().contains("IRON") && // 非铁门（重型闸门）
                !material.name().contains("CHECKPOINT"); // 非检查点门
    }

    private void bounce(Snowball ball) {
        // 实现弹跳逻辑，可以根据速度增加动量
        Vector velocity = ball.getVelocity();
        // 简单的弹跳 - 反转Y轴速度
        velocity.setY(-velocity.getY() * 0.8);
        ball.setVelocity(velocity.multiply(1.05)); // 每次弹跳稍微加速
    }

    @Override
    public void onSwitchItemBar(PlayerItemHeldEvent event, boolean check) {
        // 切换物品栏时的逻辑
        if (check) {
            // 切换到SCP-018时播放声音
            event.getPlayer().playSound(event.getPlayer().getLocation(),
                    Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
        }
    }

    // 处理爆炸激活（从高处掉落或被爆炸影响）
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item) {
            Item item = (Item) event.getEntity();
            ItemStack itemStack = item.getItemStack();

            // 检查是否是SCP-018物品
            if (isSCP018Item(itemStack)) {
                // 根据伤害原因判断是否激活
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL ||
                        event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {

                    activateSCP018FromEnvironment(item);
                }
            }
        }
    }

    private boolean isSCP018Item(ItemStack item) {
        // 根据你的物品识别逻辑实现
        return item != null && item.hasItemMeta() &&
                item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().contains("新怪谈-018");
    }

    private void activateSCP018FromEnvironment(Item item) {
        // 从环境因素激活SCP-018
        Location location = item.getLocation();
        item.remove();

        // 创建激活的SCP-018实体
        Snowball ball = location.getWorld().spawn(location, Snowball.class);
        ball.setItem(item.getItemStack());

        // 设置元数据和启动计时器（与投掷时类似）
        ball.setMetadata("SCP018", new FixedMetadataValue(SCPMain.getInstance(), true));
        ball.setMetadata("SCP018_ActivationTime", new FixedMetadataValue(SCPMain.getInstance(), System.currentTimeMillis()));

        UUID ballId = ball.getUniqueId();
        activationTime.put(ballId, System.currentTimeMillis());
        isActive.put(ballId, true);

        startExplosionTimer(ball);
    }
}