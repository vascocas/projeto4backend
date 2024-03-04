package aor.paj.proj3_vc_re_jc.enums;

public enum TaskPriority {

    LOW_PRIORITY(100),
    MEDIUM_PRIORITY(200),
    HIGH_PRIORITY(300);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TaskPriority fromValue(int value) {
        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.getValue() == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("No such priority with value: " + value);
    }
}