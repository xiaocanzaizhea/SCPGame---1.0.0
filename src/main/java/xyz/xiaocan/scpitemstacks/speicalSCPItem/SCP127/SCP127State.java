package xyz.xiaocan.scpitemstacks.speicalSCPItem.SCP127;

public enum SCP127State {
    state1(0,20,
            0.5f,25,
            2, 5),
    state2(1000, 22,
            0.3f, 35,
            4, 3),
    state3(1500, 24,
            0.2f, 50,
            5, 1);

    private float damage;
    private float rateOfFire;
    private float maxShield;
    private float rateOfAmmoRegeneration;
    private float delayOfAmmoRegeneration;
    private float needPoints;

    SCP127State(float needPoints, float damage, float rateOfFire,
                float maxShield, float rateOfAmmoRegeneration,
                float delayOfAmmoRegeneration) {
        this.damage = damage;
        this.rateOfFire = rateOfFire;
        this.maxShield = maxShield;
        this.rateOfAmmoRegeneration = rateOfAmmoRegeneration;
        this.delayOfAmmoRegeneration = delayOfAmmoRegeneration;
        this.needPoints = needPoints;
    }
}
