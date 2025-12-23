package xyz.xiaocan.scpitemstacks.weapon.gun;


import lombok.Getter;

@Getter
public enum GunType {
    //----------------九尾枪械-------------
    COM15("COM15"),
    COM18("COM18"),
    COM45("COM45"),
    CROSSVEC("CROSSVEC"),
    FSP9("FSP9"),
    MTF_E11_SR("MTFE11SR"),
    FR_MG_0("FRMG0"),
    //----------------混沌枪械-------------
    REVOLVER("REVOLVER"),
    AK("AK"),
    LOG("LOG"),
    SHOTGUN("SHOTGUN");

    private String id;
    GunType(String id){
        this.id = id;
    }
}
