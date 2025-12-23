package xyz.xiaocan.scpitemstacks.Radios;

import xyz.xiaocan.configload.option.itemoption.RadiosSetting;

public enum RadiosStates {
    SR("SR",RadiosSetting.getInstance().inSRWait,
            RadiosSetting.getInstance().inSRUsing, 108),
    MR("MR",RadiosSetting.getInstance().inMRWait,
            RadiosSetting.getInstance().inMRUsing, 180),
    LR("LR",RadiosSetting.getInstance().inLRWait,
            RadiosSetting.getInstance().inLRUsing, 1080),
    UR("UR",RadiosSetting.getInstance().inURWait,
            RadiosSetting.getInstance().inURUsing, 7200);

    public String displayName;
    public float waitPower;
    public float usePower;
    public float maxDistance;

    RadiosStates(String displayName, float waitPower, float usePower, float maxDistance) {
        this.displayName = displayName;
        this.waitPower = waitPower;
        this.usePower = usePower;
        this.maxDistance = maxDistance;
    }
}
