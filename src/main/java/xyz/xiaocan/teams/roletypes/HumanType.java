package xyz.xiaocan.teams.roletypes;

/**
 * SCP类型枚举
 */
public enum HumanType implements RoleType {
    RIFLEMAN("rifleman"),
    PREDATOR("predator"),
    SUPPRESSOR("suppressor"),
    CHAOSCONSCRIPT("chaosconscript"),
    DCLASS("dclass"),
    MTFCAPTAIN("mtfcaptain"),
    MTFPRIVATE("mtfprivate"),
    MTFPSERGEANT("mtfsergeant"),
    MTFPCONTAINMENTEXPERT("mtfcontainmentexpert"),
    GUARD("guard"),
    SCIENTIST("scientist"),
    Admin("admin");

    private final String id;

    HumanType(String id) {
        this.id = id;
    }

    @Override public String getId() { return id; }

    public static HumanType fromConfigKey(String configKey) {
        for (HumanType type : values()) {
            if (type.id.equals(configKey)) return type;
        }
        return null;
    }
}
