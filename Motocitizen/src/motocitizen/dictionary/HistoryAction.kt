package motocitizen.dictionary

enum class HistoryAction constructor(override val code: String, override val text: String): Dictionary<String> {
    CREATE("c", "Создал"),
    OPEN("a", "Отмена отбоя"),
    CLOSE("e", "Отбой"),
    HIDE("h", "Скрыл"),
    BAN("b", "Бан"),
    CANCEL("cl", "Отменил выезд"),
    FINISH("f", "Прочее"),
    IN_PLACE("i", "Приехал"),
    ON_WAY("o", "Выехал"),
    LEAVE("l", "Уехал"),
    OTHER("na", "Прочее");
}
