package xyz.xiaocan.configload;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.xiaocan.scpgame.SCPMain;
import static xyz.xiaocan.scpitemstacks.card.CardType.*;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class DefaultConfigGenerate {

    private SCPMain plugin;
    DefaultConfigGenerate(){
        this.plugin = SCPMain.getInstance();
    }

    public void init(){
        File file = new File(SCPMain.getInstance().getDataFolder(), "scpOption.yml");
        File file1 = new File(SCPMain.getInstance().getDataFolder(), "messages.yml");
        File file2 = new File(SCPMain.getInstance().getDataFolder(), "teams.yml");
        File file3 = new File(SCPMain.getInstance().getDataFolder(), "killsocre.yml");
        File file4 = new File(SCPMain.getInstance().getDataFolder(), "medicals.yml");
        File file5 = new File(SCPMain.getInstance().getDataFolder(), "cards.yml");
        File file6 = new File(SCPMain.getInstance().getDataFolder(), "doors.yml");
        File file7 = new File(SCPMain.getInstance().getDataFolder(), "guns.yml");
        File file9 = new File(SCPMain.getInstance().getDataFolder(), "grenade.yml");
        File file10 = new File(SCPMain.getInstance().getDataFolder(), "914setting.yml");
        File file11 = new File(SCPMain.getInstance().getDataFolder(), "elevatorSetting.yml");
        File file12 = new File(SCPMain.getInstance().getDataFolder(), "teamtarget.yml");

        //此文件夹存放scp设置
        File scpSettingsFolder = new File(SCPMain.getInstance().getDataFolder(), "scp_settings");
        if (!scpSettingsFolder.exists()) {
            boolean created = scpSettingsFolder.mkdirs();
            if (created) {
                Bukkit.getLogger().info("成功创建 SCP 设置文件夹");
            } else {
                Bukkit.getLogger().warning("创建 SCP 设置文件夹失败");
            }
        }
        File file8 = new File(scpSettingsFolder, "scpspeicalsetting.yml");

        checkAndGenerate(file,this::generateScpOptionDefaultConfig);
        checkAndGenerate(file1,this::generateMessagesDefaultConfig);
        checkAndGenerate(file2,this::generateTeamsDefaultConfig);
        checkAndGenerate(file3,this::generateKillScoreConfig);
        checkAndGenerate(file4,this::generateMedicalSuppliesConfig);
        checkAndGenerate(file5,this::generateCardConfig);
        checkAndGenerate(file6,this::generateDoorsConfig);
        checkAndGenerate(file7,this::generateGunConfig);
        checkAndGenerate(file8,this::generateSCPConfig);
        checkAndGenerate(file9,this::generateGrenadeConfig);
        checkAndGenerate(file10,this::generate914Config);
        checkAndGenerate(file11,this::generateElevator);
        checkAndGenerate(file12,this::generateTarget);
    }
    public void checkAndGenerate(File configFile, Consumer<File> generator){
        if(!configFile.exists()){
            generator.accept(configFile);
        }
    }
    public void generateScpOptionDefaultConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        config.set("arena-name", "SCP游戏");
        config.set("min-players", 6);
        config.set("max-players", 24);

        config.set("game.duration", 1200);
        config.set("game.respawn-time", 120.0);
        config.set("game.wait-time", 10);
        config.set("game.allow-respawn", true);
        config.set("game.friendly-fire", true);
        config.set("game.auto-balance", true);

        config.set("world.border-size", 500);
        config.set("world.allow-pvp", true);
        config.set("world.allow-pve", true);

        ConfigurationSection debug = config.createSection("debug");
        debug.set("debug", false);

        ConfigurationSection gun = config.createSection("gun");
        gun.set("gunParticalStart", 0.05);

        setupSpawnPoint(config, "lobby-spawn",
                -45.0, 52.0, 19.0, 0.0f, 0.0f);
        setupSpawnPoint(config,"escape-location",
                0.5,70.0,0.5,0.0f,0.0f);

        saveConfigFile(config, configFile, "竞技场");
    }
    private void generateMessagesDefaultConfig(File configFile) {
        FileConfiguration config = new YamlConfiguration();

        config.set("join", "&a玩家 {player} 加入了游戏");
        config.set("quit", "&c玩家 {player} 离开了游戏");
        config.set("victory", "&6{team} 队伍获得了胜利!");
        config.set("game-start", "&e游戏开始!");
        config.set("game-end", "&e游戏结束!");
        config.set("respawn-in", "&a{time}秒后复活");

        config.set("errors.insufficient-players", "&c玩家不足，无法开始游戏");
        config.set("errors.already-in-game", "&c你已经在游戏中");

        saveConfigFile(config, configFile, "消息");
    }
    private void generateTeamsDefaultConfig(File configFile) {
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection teamsSection = config.createSection("teams");
        World world = Bukkit.getWorlds().get(0);
        Location location = new Location(Bukkit.getWorlds().get(0), 100, 64, 200, -90f, 0f);

        Location chaoslocation = new Location(world, -69.0, -5.0, -102.0);
        //chaos步枪手
        setTeamConfig(teamsSection,"rifleman", chaoslocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"混沌分裂者步枪手","GREEN", "chaos");
        //chaos掠夺者
        setTeamConfig(teamsSection,"predator", chaoslocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"混沌分裂者掠夺者","GREEN", "chaos");
        //chaos机枪手
        setTeamConfig(teamsSection,"suppressor", chaoslocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"混沌分裂者机枪手","GREEN", "chaos");
        //chaos征兆兵
        setTeamConfig(teamsSection,"chaosconscript", chaoslocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"混沌分裂者征召兵","GREEN", "chaos");
        //dclass
        setTeamConfig(teamsSection,"dclass", new Location(world, 6.0, 52.0, 10.0),
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"D级人员","GOLD", "chaos");
        //科学家
        setTeamConfig(teamsSection,"scientist", new Location(world, 5.0, 52.0, -128.0),
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"科学家","WHITE", "mtf");

        Location mtflocation = new Location(world, -42.0, -5.0, -151.0);
        //mtf指挥官
        setTeamConfig(teamsSection,"mtfcaptain", mtflocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"九尾狐指挥官","BLUE", "mtf");
        //mtf列兵
        setTeamConfig(teamsSection,"mtfprivate", mtflocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"九尾狐列兵","BLUE",  "mtf");
        //mtf中士
        setTeamConfig(teamsSection,"mtfsergeant", mtflocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"九尾狐中士","BLUE",  "mtf");
        //mtf收容专家
        setTeamConfig(teamsSection,"mtfcontainmentexpert", mtflocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"九尾狐收容专家","BLUE",  "mtf");
        //guard
        setTeamConfig(teamsSection, "guard", new Location(world, 83.0, 52.0, -51.0),
                100.0f, 75.0,null,null, null, null,
                0.2f,10.0f,"警卫","BLUE",  "mtf");
        //spec
        setTeamConfig(teamsSection, "spec", location,
                100.0f, 75.0,null,null, null, null,
                0.2f,10.0f,"观察者","BLUE",  "mtf");
        //scp049
        setTeamConfig(teamsSection, "scp049", new Location(world, -106.0, 83.0, -83.0),
                1000.0f, 1000.0f,3.0,15.0, 10.0,5.0,
                0.2f, 10.0f,"乌鸦","RED",  "scp");

        //scp0492
        setTeamConfig(teamsSection, "scp0492", new Location(world, -104.0, 78.0, -104.0),
                500, 100, null,null, null,null,
                0.2f,10.0f, "小姜","RED",  "scp");

        //scp173
        setTeamConfig(teamsSection, "scp173", new Location(world, -104.0, 78.0, -104.0),
                1000.0f, 1000.0, 3.0,15.0, 10.0,5.0,
                0.2f,10.0f, "花生","RED", "scp");

        //scp106
        setTeamConfig(teamsSection, "scp106", new Location(world, -104.0, 78.0, -104.0),
                1000.0f, 1000.0, 3.0,15.0, 10.0,5.0,
                0.2f,10.0f, "老头","RED", "scp");



        saveConfigFile(config, configFile, "队伍配置");
    }
    private void generateKillScoreConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection killsocres = config.createSection("killscores");

        //dclass
        setKillScore(killsocres, "dclass", null,null,1.0,1.0,
                null,10.0,100.0,10.0,null);
        //chaos-gunner
        setKillScore(killsocres,"chaos-gunner", null,null,1.0,1.0,
                -1.0,10.0,100.0,10.0,10.0);
        //chaos-gunner2
        setKillScore(killsocres,"chaos-gunner2", null,null,1.0,1.0,
                -1.0,10.0,100.0,10.0,null);
        //mtf-soldier
        setKillScore(killsocres,"mtf-soldier", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,10.0);
        //mtf-captain
        setKillScore(killsocres,"mtf-captain", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,10.0);
        //guard
        setKillScore(killsocres,"guard", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,10.0);
        //scientist
        setKillScore(killsocres,"scientist", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,null);
        //scp173
        setKillScore(killsocres, "scp173",1.0,1.0,1.0,1.0,
                10.0,10.0,-1.0,10.0,null);
        //scp049
        setKillScore(killsocres,"scp049", 1.0,1.0,1.0,1.0,
                10.0,10.0,-1.0,10.0,null);

        saveConfigFile(config, configFile, "击杀得分");
    }
    private void generateMedicalSuppliesConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection medical = config.createSection("medicals");

        //medicalbag
        setMedical(medical, "medicalbag",
                "医疗包", 1.0,
                50.0, null, null);
        //painkiller
        setMedical(medical, "painkiller", "止痛药", 2.0,50.0, null,10.0);
        //stimulant
        setMedical(medical, "stimulant", "肾上腺素", 2.0,null,50.0, null);
        //scp500
        setMedical(medical, "scp500", "scp500", 1.0,100.0,null, null);

        saveConfigFile(config, configFile,"医疗物品");
    }
    public void generateCardConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection cards = config.createSection("cards");

        //紫卡-清洁工
        setCards(cards,"Janitor","清洁工钥匙卡","PAPER",List.of(1,0,0),0);
        //黄卡-科学家
        setCards(cards, "Scientist","科学家钥匙卡","PAPER", List.of(2,0,0),1);
        //橙卡-研究员
        setCards(cards, "Research","研究主管钥匙卡","PAPER", List.of(2,0,1),2);
        //绿卡-区域卡
        setCards(cards, "Zone","区域总监钥匙卡","PAPER", List.of(1,0,1),3);
        //灰卡-安保卡
        setCards(cards, "Guard","设施警卫钥匙卡","PAPER", List.of(1,1,1),4);
        //青卡-新兵卡
        setCards(cards, "Private1","九尾狐列兵钥匙卡","PAPER", List.of(2,2,2),5);
        //绿卡-收容工程师
        setCards(cards, "Engineer","收容工程师钥匙卡","PAPER", List.of(3,0,1),6);
        //浅蓝卡-九尾狐特工
        setCards(cards, "Mtfspy","MTF特工钥匙卡","PAPER", List.of(2,2,1),7);
        //蓝卡-指挥官卡
        setCards(cards, "Commander","九尾狐指挥官钥匙卡","PAPER", List.of(2,3,2),8);
        //红卡-设施卡
        setCards(cards, "Facility","设施总监钥匙卡","PAPER", List.of(3,0,3),9);
        //混沌-解码器
        setCards(cards, "Decoder","混沌分裂者破译装置","PAPER", List.of(2,3,2),10);
        //黑卡-O5议会卡
        setCards(cards, "O5","O5钥匙卡","PAPER", List.of(3,3,3),11);


        saveConfigFile(config, configFile,"卡片模版");
    }
    public void generateGrenadeConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection grenade = config.createSection("grenade");

        //手雷设置
        setGrenade(grenade, "grenade", "破片手雷",
                "SNOWBALL", 1, null,
                80.0, 5.0,3.0);

        saveConfigFile(config, configFile,"手雷设置完毕");
    }
    public void generateDoorsConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection doors = config.createSection("doors");

        //doortemp
        setDoors(doors, "doortemp", "BLACK_CONCRETE_POWDER",true, false,
                0.25,
                true,-1.0,"rotateright","",
                true,true,1.5,
                0.5,0.5,Arrays.asList(0,0,0));

        setDoors(doors, "level", "DEEPSLATE",true, true,
                0.25,
                true,-1.0,"rotateright","LEVEL_LOCK_ONE_BLUE",
                true,false,1.5,
                0.5,0.5,Arrays.asList(0,0,0));

        setDoors(doors, "light", "QUARTZ_BLOCK",true, true,
                0.25,
                true,-1.0,"rotateright","NO_LEVEL_LOCK_LIGHT_OFF",
                true,true,1.5,
                0.5,0.5,Arrays.asList(0,0,0));

        setDoors(doors, "elevator", "WHITE_CONCRETE_POWDER",false, false,
                0.25,
                true,-1.0,"rotateright","ELEVATOR_LOCK",
                true,false,1.5,
                0.5,0.5,Arrays.asList(0,0,0));

        setDoors(doors, "heavy", "BLACK_CONCRETE_POWDER",true, true,
                0.25,
                true,-1.0,"rotateright","HEAVY_LOCK_BLUE",
                true,true,1.5,
                0.5,0.5,Arrays.asList(0,0,0));

        saveConfigFile(config, configFile,"门模版");
    }
    public void generateGunConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection guns = config.createSection("guns");
        ConfigurationSection ammo = config.createSection("ammo");

        //gun
        setGun(guns, "COM15","COM-15",25.0,4.0,
                "A919",  0.1, 12,  "LAVA", 0,
                0.2, 0.7,
                "scp:weapon.com.fire", "scp:weapon.com.equip");

        setGun(guns, "COM18","COM-18",21.2,4.0,
                "A919",  0.1, 15,  "LAVA", 1,
                0.2, 0.7,
                "scp:weapon.com.fire", "scp:weapon.com.equip");

        setGun(guns, "FSP9","冲锋枪",22.3,3,
                "A919",  0.05, 30, "LAVA", 2,
                0.2, 0.7,
                "scp:weapon.crossvec.fire", "scp:weapon.crossvec.equip");

        setGun(guns, "CROSSVEC","Crossvec冲锋枪",23.0,4.25,
                "A919",  0.05, 40, "LAVA", 2,
                0.2, 0.7,
                "scp:weapon.fsp9.fire", "scp:weapon.fsp9.equip");

        setGun(guns, "COM45","COM-45",75.0,3,
                "A919",  0.1, 12,  "LAVA", 100,
                0.2, 0.7,
                "scp:weapon.com.fire", "scp:weapon.com.equip");

        //-----------------------------------------混沌枪械----------------------------------------
        setGun(guns, "REVOLVER",".44左轮手枪",58.0,6,
                "A444",  0.5, 6,  "LAVA", 10,
                0.1, 0.3,
                "scp:weapon.44.fire", "scp:weapon.44.cook");

        setGun(guns, "AK","突击步枪",26.2,2,
                "A762",  0.5, 31,  "LAVA", 11,
                0.1, 0.3,
                "scp:weapon.ak.fire", "scp:weapon.ak.equip");

        setGun(guns, "LOG","轻机枪",26.8,4,
                "A762",  0.3, 100,  "LAVA", 12,
                0.1, 0.3,
                "scp:weapon.log.fire", "scp:weapon.log.equip");

        //todo,改一下射击轨迹等
        setGun(guns, "SHOTGUN","泵动式霰弹枪",66.64,10,
                "A762",  0.3, 14,  "LAVA", 13,
                0.1, 0.3,
                "scp:weapon.shotgun.fire", "scp:weapon.shotgun.equip");

        //ammo
        setAmmo(ammo,"A919", "9✖19毫米弹药", 70, 50);
        setAmmo(ammo,"A556", "5.56✖45毫米弹药", 40, 50);
        setAmmo(ammo,"A762", "7.62✖39毫米弹药", 40, 50);
        setAmmo(ammo,"A12", "12/70弹药", 14, 50);
        setAmmo(ammo,"A444", ".44Mag", 18, 50);

        saveConfigFile(config, configFile, "枪械和弹药列表");
    }
    public void generate914Config(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection scp914Setting = config.createSection("scp914setting");

        setScp914Setting(scp914Setting,4.0,8.0);

        ConfigurationSection cards = config.createSection("cards");

        String janitor = Janitor.id;
        String scientist = Scientist.id;
        String zone = Zone.id;
        String gurad = Guard.id;
        String research = Research.id;
        String private1 = Private1.id;
        String engineer = Engineer.id;
        String mtfspy = Mtfspy.id;
        String commander = Commander.id;
        String facility = Facility.id;
        String decoder = Decoder.id;
        String o5 = O5.id;

        set914Config(cards,janitor,
                Arrays.asList("[DESTROY]"), Arrays.asList("[DESTROY]"),
                Arrays.asList(zone), Arrays.asList(scientist),
                Arrays.asList(scientist, research));

        set914Config(cards, scientist,
                Arrays.asList("[DESTROY]", janitor), Arrays.asList(janitor),
                Arrays.asList(zone), Arrays.asList(research),
                Arrays.asList(scientist, research, facility));

        set914Config(cards, research,
                Arrays.asList(scientist, janitor),
                Arrays.asList(scientist),
                Arrays.asList(gurad),
                Arrays.asList(facility),
                Arrays.asList(research, facility));

        set914Config(cards, gurad,
                Arrays.asList(scientist, janitor),
                Arrays.asList(scientist),
                Arrays.asList(research),
                Arrays.asList(mtfspy),
                Arrays.asList(gurad, mtfspy, commander));

        set914Config(cards, mtfspy,
                Arrays.asList(gurad, "[DESTROY]"),
                Arrays.asList(private1),
                Arrays.asList(facility),
                Arrays.asList(commander),
                Arrays.asList(o5, mtfspy, commander));

        set914Config(cards, commander,
                Arrays.asList(mtfspy),
                Arrays.asList(mtfspy),
                Arrays.asList(decoder),
                Arrays.asList(o5),
                Arrays.asList(o5, o5, o5, "[DESTROY]"));

        set914Config(cards, zone,
                Arrays.asList(janitor, scientist),
                Arrays.asList(scientist),
                Arrays.asList(gurad),
                Arrays.asList(facility),
                Arrays.asList(zone, zone, facility, facility, decoder));

        set914Config(cards, facility,
                Arrays.asList(zone),
                Arrays.asList(commander),
                Arrays.asList(decoder),
                Arrays.asList(o5),
                Arrays.asList(o5, o5, o5, "[DESTROY]"));

        set914Config(cards, decoder,
                Arrays.asList(gurad),
                Arrays.asList(mtfspy),
                Arrays.asList(commander),
                Arrays.asList(o5),
                Arrays.asList(o5, o5, o5, "[DESTROY]"));

        set914Config(cards, o5,
                Arrays.asList(gurad),
                Arrays.asList(facility, commander),
                Arrays.asList(o5),
                Arrays.asList(o5, "[DESTROY]"),
                Arrays.asList(o5, o5, o5, "[DESTROY]"));

        saveConfigFile(config, configFile, "914转化列表");
    }
    public void generateSCPConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection scp049 = config.createSection("scp049");

        ConfigurationSection scp173 = config.createSection("scp173");

        ConfigurationSection scp106 = config.createSection("scp106");

        setSCP049(scp049,"scp049", 176.0, 22.0, 5,
                30.0,30.0, 0.1,
                3.0,700, 20.0,20.0,
                5.0);

        setSCP173(scp173, "scp173",200.0, 3.0, 3.0,6,20.0
        ,100.0,20.0,15.0,0.2, 4, 0.5);

        setSCP106(scp106,"scp106", 1.2f, 2.0f, 0.2f,0.3f);

        saveConfigFile(config, configFile, "scp的特殊设置");
    }
    public void generateElevator(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection elevator = config.createSection("elevator");

        setElevator(elevator,8.0);

        saveConfigFile(config, configFile, "电梯的设置");
    }
    public void generateTarget(File configFile){
        FileConfiguration config = new YamlConfiguration();

        //General target
        ConfigurationSection killscp = config.createSection("killscp");
        ConfigurationSection killscp0492 = config.createSection("killscp0492");
        ConfigurationSection damagescp = config.createSection("damagescp");
        ConfigurationSection usespeicalitemkillscp = config.createSection("usespeicalitemkillscp");
        ConfigurationSection firstusescpitem = config.createSection("firstusescpitem");
        ConfigurationSection useconsumescpitem = config.createSection("useconsumescpitem");
        ConfigurationSection killpeople = config.createSection("killpeople");
        ConfigurationSection activategenerator = config.createSection("activategenerator");
        ConfigurationSection killunarmedpeople = config.createSection("killunarmedpeople");

        //mtf target
        ConfigurationSection scientistescape = config.createSection("scientistescape");
        ConfigurationSection bindingdclassescape = config.createSection("bindingdclassescape");

        //chaos target
        ConfigurationSection dclassescape = config.createSection("dclassescape");

        setTarget(killscp, -10,15);
        setTarget(killscp0492, -5,0);
        setTarget(damagescp, -2,1);
        setTarget(usespeicalitemkillscp, 0,5);
        setTarget(firstusescpitem, -5,2);
        setTarget(useconsumescpitem, -2,1);
        setTarget(killpeople, -4,1);
        setTarget(activategenerator, -10,3);
        setTarget(killunarmedpeople, 10,-5);
        setTarget(scientistescape, -30,5);
        setTarget(bindingdclassescape, -10,5);
        setTarget(dclassescape, -20,5);
        saveConfigFile(config, configFile, "电梯的设置");
    }
    public void generateWeaponSetting(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection cagedbird = config.createSection("weapon");

        setCagedBird(cagedbird, 50.0,
                200.0,2.0,4); //设置囚鸟

        saveConfigFile(config, configFile, "电梯的设置");
    }
    public void setElevator(ConfigurationSection key, double runningTime){
        key.set("runningTime", runningTime);
    }
    public void setTarget(ConfigurationSection key, int time, int getPoint){
        key.set("time", time);
        key.set("getPoint", getPoint);
    }
    public void setCagedBird(ConfigurationSection key, double normalDamage,
                             double chargeDamage, double chargeTime, double specialDamageTo49_2){
        key.set("normalDamage", normalDamage);
        key.set("chargeDamage", chargeDamage);
        key.set("chargeTime", chargeTime);
        key.set("specialDamageTo49_2", specialDamageTo49_2);
    }

    public void setSCP049(ConfigurationSection key, String id,
                          double damage, double damageDuringTime, double attackCooldown,
                            double fSkillDuringTime, double fSkillCooldown, double speedAdd,
                              double rSkillRadius,double totalShield, double rSkillDuringTime, double rSkillColldown,
                                double helpTime){
        key.set("id", id);

        ConfigurationSection normalAttack = key.createSection("normalattack");
        normalAttack.set("damage", damage);
        normalAttack.set("damageDuringTime", damageDuringTime);
        normalAttack.set("attackCooldown", attackCooldown);

        ConfigurationSection fSkill = key.createSection("fskill");
        fSkill.set("fSkillDuringTime", fSkillDuringTime);
        fSkill.set("fSkillCooldown", fSkillCooldown);
        fSkill.set("fSkillSpeedAdd", speedAdd);

        ConfigurationSection rSkill = key.createSection("rskill");
        rSkill.set("rSkillRadius", rSkillRadius);
        rSkill.set("totalShield", totalShield);
        rSkill.set("rSkillDuringTime", rSkillDuringTime);
        rSkill.set("rSkillColldown", rSkillColldown);

        key.set("helpTime", helpTime);
    }

    public void setSCP106(ConfigurationSection key, String id,
                          float attackCD, float skillCD,
                          float moveSpeed, float moveSpeedDuringSkill){
        key.set("id", id);
        key.set("attackCD", attackCD);
        key.set("skillCD", skillCD);
        key.set("moveSpeed", moveSpeed);
        key.set("moveSpeedDuringSkill", moveSpeedDuringSkill);
    }

    private void setSCP173(ConfigurationSection key, String id,
                           double damage, double radius,
                             double cdOfTeleport, double teleportDistance,
                               double cdOfMud, double mudDuringTime,
                                 double cdOfHighSpeed, double highSpeedDuringTime,
                                    double highSpeedAdd, double highSpeedDistanceAdd, double percentOfTeleportTime){
        key.set("id", id);
        key.set("damage", damage);
        key.set("radius", radius);

        ConfigurationSection tp = key.createSection("tp");
        tp.set("cdOfTeleport", cdOfTeleport);
        tp.set("teleportDistance", teleportDistance);

        ConfigurationSection mud = key.createSection("mud");
        mud.set("cdOfMud", cdOfMud);
        mud.set("mudDuringTime", mudDuringTime);

        ConfigurationSection highspeed = key.createSection("highspeed");
        highspeed.set("cdOfHighSpeed", cdOfHighSpeed);
        highspeed.set("highSpeedDuringTime", highSpeedDuringTime);
        highspeed.set("highSpeedAdd", highSpeedAdd);
        highspeed.set("highSpeedDistanceAdd", highSpeedDistanceAdd);
        highspeed.set("percentOfTeleportTime", percentOfTeleportTime);

    }
    public void setGun(ConfigurationSection key, String id, String disPlayName,
                       double damage, double reloadTime, String AmmoTypeId, double rateOfFire, int maxAmmo,
                       String partical, int customModelData,
                       double aimingAccuracy, double waistShootAccuracy, String fireSound, String equipSound){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("damage", damage);
        key2.set("reloadTime", reloadTime);
        key2.set("ammoType", AmmoTypeId);
        key2.set("rateOfFire", rateOfFire);
        key2.set("maxAmmo", maxAmmo);
        key2.set("partical", partical);
        key2.set("customModelData", customModelData);
        key2.set("aimingAccuracy", aimingAccuracy);
        key2.set("waistShootAccuracy", waistShootAccuracy);
        key2.set("fireSound", fireSound);
        key2.set("equipSound", equipSound);
    }

    public void setScp914Setting(ConfigurationSection key, double startTime, double handleTime){
        key.set("starttime",startTime);
        key.set("handletime",handleTime);
    }
    public void setAmmo(ConfigurationSection key, String id, String disPlayName, int maxAmmoTake, double maxDistance){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("maxAmmoTake", maxAmmoTake);
        key2.set("maxDistance", maxDistance);
    }
    public void set914Config(ConfigurationSection key, String id,
                             List<String> roughCards, List<String> halfRoughCards,
                             List<String> cards11, List<String> refineCards, List<String> superRefineCards){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("rough", roughCards);
        key2.set("halfrough", halfRoughCards);
        key2.set("cards11", cards11);
        key2.set("fine", refineCards);
        key2.set("veryfine", superRefineCards);
    }
    public void setGrenade(ConfigurationSection key, String id,
                           String disPlayName, String material, int customModelData, List<String> lore,
                           double damage, double radius, double explosionTime){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("material", material);
        key2.set("customModelData", customModelData);
        key2.set("lore", lore);
        key2.set("damage", damage);
        key2.set("radius", radius);
        key2.set("explosionTime", explosionTime);
    }

    public void setSmokeGrenade(ConfigurationSection key, String id, String disPlayName, double smokeDuration, double radius, double explosionTime){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("smokeDuration", smokeDuration);
        key2.set("radius", radius);
        key2.set("explosionTime", explosionTime);
    }

    public void setDoors(ConfigurationSection key, String id, String material, boolean canBreak, boolean canOpenByPlayer,
                         double itemDisplaySize,
                         boolean needAnimation, double autoClosed, String doorMoveMode, String doorLock,
                         boolean needIcon, boolean needAnimationButton,
                         double animationtime, double boxdisappeartime_scale,
                         double boxappeartime_scale, List<Integer> permissionsLevel){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("material", material);
        key2.set("canBreak", canBreak);
        key2.set("canOpenByPlayer", canOpenByPlayer);
        key2.set("itemDisplaySize", itemDisplaySize);
        key2.set("permissionsLevel", permissionsLevel);

        ConfigurationSection key3 = key2.createSection("needAnimation");
        key3.set("needAnimation", needAnimation);

        ConfigurationSection key4 = key3.createSection("animtime");
        key4.set("animationtime", animationtime);
        key4.set("boxdisappeartime_scale", boxdisappeartime_scale);
        key4.set("boxappeartime_scale", boxappeartime_scale);

        ConfigurationSection key5 = key3.createSection("soundeffect");
        key5.set("open", "BLOCK_IRON_DOOR_OPEN");
        key5.set("closed", "BLOCK_IRON_DOOR_CLOSE");
        key5.set("fail", "ENTITY_VILLAGER_NO");

        key3.set("autoClosed", autoClosed);
        key3.set("doorMoveMode", doorMoveMode);
        key3.set("doorLock", doorLock);
        key3.set("needIcon", needIcon);
        key3.set("needAnimationButton", needAnimationButton);
    }

    public void setCards(ConfigurationSection key, String id, String disPlayName, String material, List<Integer> permissionsLevel, int custommodeldata){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id",id);
        key2.set("disPlayName", disPlayName);
        key2.set("material", material);
        key2.set("permissionsLevel", permissionsLevel);
        key2.set("custommodeldata", custommodeldata);
    }
    public void setMedical(ConfigurationSection key, String id, String disPlayName, Double usageTime, Double healingHp, Double healingShield, Double duringTime){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("usageTime", usageTime);
        key2.set("healingHp", healingHp);
        if(duringTime!=null) key2.set("duringTime", duringTime);
        if(healingShield!=null) key2.set("healingShield", healingShield);
    }

    public void setKillScore(ConfigurationSection key, String id,Double chaos_gunner,Double chaos_gunner2, Double mtf_soldier,
                             Double mtf_captain, Double d, Double scientist, Double scp, Double winscore, Double speicalsocre){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id",id);
        key2.set("chaos_gunner", chaos_gunner!=null ? chaos_gunner : null);
        key2.set("chaos_gunner2", chaos_gunner2!=null ? chaos_gunner2 : null);
        key2.set("mtf_soldier", mtf_soldier!=null ? mtf_soldier : null);
        key2.set("mtf_captain", mtf_captain!=null ? mtf_captain : null);
        key2.set("d", d!=null ? d : null);
        key2.set("scientist", scientist!=null ? scientist : null);
        key2.set("scp", scp!=null ? scp : null);
        key2.set("winscore", winscore!=null ? winscore : null);
        key2.set("speicalscore", speicalsocre!=null ? speicalsocre : null);

    }

    public void setTeamConfig(ConfigurationSection key, String id, Location location,
                              double maxHp, double maxShield, Double healHpCount, Double recoverShieldCount, Double healHpNeedTime, Double recoverShieldNeedTime,
                              double moveSpeed, double armor, String disPlayName, String Color,  String camp){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("displayName", disPlayName);

        ConfigurationSection hpKey = key2.createSection("hp");
        hpKey.set("hp", maxHp);
        if(healHpCount!=null)hpKey.set("healHpCount", healHpCount);
        if(healHpNeedTime!=null)hpKey.set("healHpNeedTime", healHpNeedTime);

        ConfigurationSection shieldKey = key2.createSection("shield");
        shieldKey.set("shield", maxShield);
        if(healHpCount!=null)shieldKey.set("recoverShieldCount", recoverShieldCount);
        if(recoverShieldNeedTime!=null)shieldKey.set("recoverShieldNeedTime", recoverShieldNeedTime);

        key2.set("armor", armor);
        key2.set("movespeed", moveSpeed);
        key2.set("color", Color);
        key2.set("camp", camp);

        ConfigurationSection spawnpoint = key2.createSection("spawnpoint");
        spawnpoint.set("x", location.getX());
        spawnpoint.set("y", location.getY());
        spawnpoint.set("z", location.getZ());
        spawnpoint.set("yaw", location.getYaw());
        spawnpoint.set("pitch", location.getPitch());

    }

    private void setupSpawnPoint(ConfigurationSection parent, String key,
                                 double x, double y, double z, float yaw, float pitch) {
        ConfigurationSection spawn = parent.createSection(key);
        spawn.set("x", x);
        spawn.set("y", y);
        spawn.set("z", z);
        spawn.set("yaw", yaw);
        spawn.set("pitch", pitch);
    }

    private void saveConfigFile(FileConfiguration config, File configFile, String Name) {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            config.save(configFile);
            SCPMain.getInstance().getLogger().info("" + Name + "默认配置文件已生成: " + configFile.getName());
        } catch (IOException e) {
            SCPMain.getInstance().getLogger().log(Level.SEVERE, "无法保存配置文件: " + configFile.getName(), e);
        }
    }
}
