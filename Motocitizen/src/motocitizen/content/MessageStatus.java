package motocitizen.content;

public enum MessageStatus {
    HIDDEN, ACTIVE;


    public static final String hidden = "hidden";
    public static final String active = "active";

    @Override
    public String toString() {
        return getString(this);
    }

    public String toCode() {
        return getCode(this);
    }

    public static String getString(MessageStatus status) {
        switch (status) {
            case HIDDEN:
                return "Скрыто";
            case ACTIVE:
            default:
                return "Активно";
        }
    }

    public static String getCode(MessageStatus status) {
        switch (status) {
            case HIDDEN:
                return hidden;
            case ACTIVE:
            default:
                return active;
        }
    }

    public static MessageStatus parse(String status) {
        switch (status) {
            case hidden:
                return HIDDEN;
            case active:
            default:
                return ACTIVE;
        }
    }
}
