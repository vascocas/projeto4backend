package aor.paj.proj3_vc_re_jc.enums;

public enum UserRole {

    DEVELOPER(100),
    SCRUM_MASTER(200),
    PRODUCT_OWNER(300);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole valueOf(int value) {
        for (UserRole role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value + " found");
    }
}