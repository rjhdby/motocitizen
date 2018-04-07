package motocitizen.dictionary

enum class AccidentStatus constructor(override val code: String, override val text: String) : Dictionary<String> {
    ACTIVE("a", "Активный"),
    ENDED("e", "Отбой"),
    HIDDEN("h", "Скрыт");
//    CONFLICT("w", "Конфликт"),
//    DUPLICATE("d", "Дубль");
}