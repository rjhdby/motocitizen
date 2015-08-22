package motocitizen.content;

public enum MessageStatus {
    HIDDEN, ACTIVE;


    public static final String hidden = "hidden";
    public static final String active = "active";

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
