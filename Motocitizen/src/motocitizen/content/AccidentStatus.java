package motocitizen.content;

public enum AccidentStatus {
    ACTIVE, ENDED, HIDDEN, CONFLICT, DUPLICATE;

    public static final String ended     = "acc_status_end";
    public static final String hidden    = "acc_status_hide";
    public static final String duplicate = "acc_status_dbl";
    public static final String conflict  = "acc_status_war";
    public static final String active    = "acc_status_act";

    public static AccidentStatus parse(String status) {
        switch (status) {
            case ended:
                return ENDED;
            case hidden:
                return HIDDEN;
            case duplicate:
                return HIDDEN; //TODO на будущее
            case conflict:
                return CONFLICT;
            case active:
            default:
                return ACTIVE;
        }
    }

    public String getStatusCode() {
        return getStatusCode(this);
    }

    public static String getStatusCode(AccidentStatus status) {
        switch (status) {
            case ENDED:
                return ended;
            case HIDDEN:
                return hidden;
            case DUPLICATE:
                return duplicate; //TODO на будущее
            case CONFLICT:
                return conflict;
            case ACTIVE:
            default:
                return active;
        }
    }

    @Override
    public String toString() {
        return getStatusString(this);
    }

    public static String getStatusString(AccidentStatus status) {
        switch (status) {
            case ACTIVE:
                return "Активный";
            case ENDED:
                return "Отбой";
            case HIDDEN:
                return "Скрыт";
            case CONFLICT:
                return "Конфликт";
            case DUPLICATE:
                return "Дубль";
            default:
                return "Активный";
        }
    }
}

