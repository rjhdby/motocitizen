package motocitizen.app.general.user;

public enum Role {
    RO("readonly", "только чтение"),
    BANNED("banned", "забанен"),
    STANDARD("standart", "пользователь"),
    MODERATOR("moderator", "модератор"),
    ADMINISTRATOR("admin", "администратор"),
    DEVELOPER("developer", "разработчик");

    private final String code;
    private final String text;

    Role(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public static Role parse(String role) {
        for (Role a : Role.values()) {
            if (a.code.equals(role)) return a;
        }
        return RO;
    }

    public boolean isRO() {
        return this == RO;
    }

    public boolean isStandart() {
        return this.compareTo(STANDARD) >= 0;
    }

    public boolean isModerator() {
        return this.compareTo(MODERATOR) >= 0;
    }

    public boolean isAdmin() {
        return this.compareTo(ADMINISTRATOR) >= 0;
    }

    public boolean isDeveloper() {
        return this.compareTo(DEVELOPER) >= 0;
    }

    public String getName() {
        return this.text;
    }

    public String getCode() {
        return code;
    }

}
