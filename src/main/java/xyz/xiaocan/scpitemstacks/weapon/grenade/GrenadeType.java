package xyz.xiaocan.scpitemstacks.weapon.grenade;

public enum GrenadeType {
    GRENADE("grenade"),
    FLASHBOMB("flashbomb");

    private String id;

    GrenadeType(String id) {
        this.id = id;
    }
}
