package motocitizen.content;

public enum MessageStatus {
    HIDDEN("hidden", "Скрыто"),
    ACTIVE("active", "Активно");

    private final String code;
    private final String text;

    MessageStatus(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public static MessageStatus parse(String status) {
        for (MessageStatus a : MessageStatus.values()) {
            if (a.code.equals(status)) return a;
        }
        return MessageStatus.ACTIVE;
    }

    public String string() {
        return this.text;
    }

    public String code() {
        return this.code;
    }
}
