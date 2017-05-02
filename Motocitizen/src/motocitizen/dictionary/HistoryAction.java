package motocitizen.dictionary;

public enum HistoryAction {
    CREATE("create_mc_acc", "Создал"),
    OPEN("acc_status_act", "Отмена отбоя"),
    CLOSE("acc_status_end", "Отбой"),
    HIDE("acc_status_hide", "Скрыл"),
    BAN("ban", "Бан"),
    CANCEL("cancel", "Отменил выезд"),
    FINISH("finish_mc_acc", "Прочее"),
    IN_PLACE("inplace", "Приехал"),
    ON_WAY("onway", "Выехал"),
    LEAVE("leave", "Уехал"),
    OTHER("other", "Прочее");

    private final String code;
    public final String text;

    HistoryAction(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public static HistoryAction parse(String action) {
        for (HistoryAction a : HistoryAction.values()) {
            if (a.code.equals(action)) return a;
        }
        return HistoryAction.OTHER;
    }
}
