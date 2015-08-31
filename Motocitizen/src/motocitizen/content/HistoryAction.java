package motocitizen.content;

public enum HistoryAction {
    CREATE, OPEN, CLOSE, HIDE, BAN, CANCEL, FINISH, INPLACE, ONWAY, LEAVE, OTHER;

    public static final String create  = "create_mc_acc";
    public static final String open    = "acc_status_act";
    public static final String close   = "acc_status_end";
    public static final String hide    = "acc_status_hide";
    public static final String ban     = "ban";
    public static final String inplace = "inplace";
    public static final String leave   = "leave";
    public static final String onway   = "onway";
    public static final String cancel  = "cancel";
    public static final String finish  = "finish_mc_acc";
    public static final String other   = "other";

    @Override
    public String toString() {
        return getActionString(this);
    }

    public String toCode() {
        return getCode(this);
    }

    public static HistoryAction parse(String action) {
        switch (action) {
            case create:
                return CREATE;
            case open:
                return OPEN;
            case close:
                return CLOSE;
            case hide:
                return HIDE;
            case ban:
                return BAN;
            case inplace:
                return INPLACE;
            case leave:
                return LEAVE;
            case onway:
                return ONWAY;
            case cancel:
                return CANCEL;
            case finish:
                return FINISH;
            default:
                return OTHER;
        }
    }

    public static String getCode(HistoryAction action) {
        switch (action) {
            case CREATE:
                return create;
            case OPEN:
                return open;
            case CLOSE:
                return close;
            case HIDE:
                return hide;
            case BAN:
                return ban;
            case INPLACE:
                return inplace;
            case LEAVE:
                return leave;
            case ONWAY:
                return onway;
            case CANCEL:
                return cancel;
            case FINISH:
                return finish;
            default:
                return other;
        }
    }

    public static String getActionString(HistoryAction action) {
        switch (action) {
            case CREATE:
                return "Создал";
            case OPEN:
                return "Отмена отбоя";
            case CLOSE:
                return "Отбой";
            case HIDE:
                return "Скрыл";
            case BAN:
                return "Бан";
            case INPLACE:
                return "Приехал";
            case LEAVE:
                return "Уехал";
            case ONWAY:
                return "Выехал";
            case CANCEL:
            case FINISH:
            case OTHER:
            default:
                return "Прочее";
        }
    }
}
