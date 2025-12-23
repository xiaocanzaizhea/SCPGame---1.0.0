package xyz.xiaocan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.xiaocan.configload.option.itemoption.RadiosSetting;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP1344Setting;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP1576Setting;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP207Setting;
import xyz.xiaocan.configload.option.itemoption.speicalSCPItem.SCP268Setting;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpitemstacks.Radios.Radio;
import xyz.xiaocan.scpitemstacks.speicalSCPItem.SCP1344;
import xyz.xiaocan.scpitemstacks.speicalSCPItem.SCP1576;
import xyz.xiaocan.scpitemstacks.speicalSCPItem.SCP207;
import xyz.xiaocan.scpitemstacks.speicalSCPItem.SCP268;
import xyz.xiaocan.scpitemstacks.weapon.gun.*;
import xyz.xiaocan.scpitemstacks.weapon.gun.chaosGuns.AK;
import xyz.xiaocan.scpitemstacks.weapon.gun.chaosGuns.LOG;
import xyz.xiaocan.scpitemstacks.weapon.gun.chaosGuns.Revolver;
import xyz.xiaocan.scpitemstacks.weapon.gun.chaosGuns.ShotGun;
import xyz.xiaocan.scpitemstacks.weapon.gun.mtfGuns.*;

public class ScptestCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player)commandSender;

        ItemManager gunManager = ItemManager.getInstance();
        Com15 com15 = new Com15(gunManager.allGuns.get(GunType.COM15));
        Com18 com18 = new Com18(gunManager.allGuns.get(GunType.COM18));
        Com45 com45 = new Com45(gunManager.allGuns.get(GunType.COM45));
        CROSSVEC crossvec = new CROSSVEC(gunManager.allGuns.get(GunType.CROSSVEC));
        FSP9 fsp9 = new FSP9(gunManager.allGuns.get(GunType.FSP9));
        //----------------
        Revolver revolver = new Revolver(gunManager.allGuns.get(GunType.REVOLVER));
        AK ak = new AK(gunManager.allGuns.get(GunType.AK));
        LOG log = new LOG(gunManager.allGuns.get(GunType.LOG));
        ShotGun shotGun = new ShotGun(gunManager.allGuns.get(GunType.SHOTGUN));
        SCP268 scp268 = new SCP268(SCP268Setting.getInstance());
        SCP207 scp207 = new SCP207(SCP207Setting.getInstance());
        SCP1576 scp1576 = new SCP1576(SCP1576Setting.getInstance());
        SCP1344 scp1344 = new SCP1344(SCP1344Setting.getInstance());
        Radio radio = new Radio(RadiosSetting.getInstance());

        player.getInventory().addItem(com15.createItemStack()
                , com18.createItemStack(), com45.createItemStack(),
                crossvec.createItemStack(), fsp9.createItemStack()

                ,revolver.createItemStack(), ak.createItemStack(),
                log.createItemStack(), shotGun.createItemStack(),

                scp268.createItemStack(),
                scp207.createItemStack(),
                scp1576.createItemStack(),
                scp1344.createItemStack(),

                radio.createItemStack()
                );


        return true;
    }
}