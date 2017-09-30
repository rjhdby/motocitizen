package motocitizen.dictionary

enum class AccidentStatus constructor(val code: String, val text: String) {
    ACTIVE("a", "Активный"),
    ENDED("e", "Отбой"),
    HIDDEN("h", "Скрыт");
//    CONFLICT("w", "Конфликт"),
//    DUPLICATE("d", "Дубль");

    companion object {
        fun parse(status: String): AccidentStatus = AccidentStatus.values().firstOrNull { it.code == status } ?: AccidentStatus.ACTIVE
    }
}