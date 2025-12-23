package xyz.xiaocan.scpitemstacks.weapon.gun;

import lombok.Getter;

@Getter
public enum AmmoType {

    A919("A919",11),
    A556("A556",12),
    A762("A762",13),
    A12("A12",14),
    A444("A444",15);

    private final String id;
    private final int slot;

    AmmoType(String id,int slot) {
        this.id = id;
        this.slot = slot;
    }

    public static AmmoType getByEnumName(String name) {
        try {
            return AmmoType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static AmmoType getBySlot(int slot){
        for (AmmoType value : AmmoType.values()) {
            if(value.slot==slot){
                return value;
            }
        }
        return null;
    }
}
