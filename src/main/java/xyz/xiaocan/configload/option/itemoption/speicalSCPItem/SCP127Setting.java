package xyz.xiaocan.configload.option.itemoption.speicalSCPItem;

public class SCP127Setting {
    private static SCP127Setting instance;

    public int killPlayerAndLevelUpGiveAmmoCnt;
    public float HSAttenuationSpeed;

    public SCP127Setting() {
        this.killPlayerAndLevelUpGiveAmmoCnt = 15;
        this.HSAttenuationSpeed = 10;
    }

    public static SCP127Setting getInstance(){
        if(instance == null){
            instance = new SCP127Setting();
        }
        return instance;
    }
}
