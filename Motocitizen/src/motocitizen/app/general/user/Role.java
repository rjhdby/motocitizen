package motocitizen.app.general.user;

import motocitizen.content.Content;

public enum Role {
    RO("только чтение", "readonly"),
    BANNED("забанен", "banned"),
    STANDARD("пользователь", "standart"),
    MODERATOR("standart", "модератор"),
    ADMINISTRATOR("admin", "администратор"),
    DEVELOPER("developer", "разработчик");

    private final String code;
    private final String text;

    Role(String code, String text) {
        this.code = code;
        this.text = text;
    }

    private static final String[] ReadOnly  = new String[]{"readonly", "banned", "standart", "moderator", "admin", "developer"};
    private static final String[] Standart  = new String[]{"standart", "moderator", "admin", "developer"};
    private static final String[] Moderator = new String[]{"moderator", "admin", "developer"};
    private static final String[] Admin     = new String[]{"admin", "developer"};
    private static final String[] Developer = new String[]{"developer"};

    public static Role parse(String role) {
        for (Role a : Role.values()) {
            if (a.code.equals(role)) return a;
        }
        return Role.RO;
    }

    public boolean isRO() {
        return this.compareTo(RO) >= 0;
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

    public String getText() {
        return text;
    }
}
