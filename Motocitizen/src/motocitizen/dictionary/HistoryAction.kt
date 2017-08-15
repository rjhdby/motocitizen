package motocitizen.dictionary

enum class HistoryAction constructor(private val code: String, val text: String) {
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

    companion object {
        fun parse(action: String): HistoryAction {
            return HistoryAction.values().firstOrNull { it.code == action }
                   ?: HistoryAction.OTHER
        }
    }
}
