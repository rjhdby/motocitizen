package motocitizen.dictionary

enum class Medicine constructor(override val code: String, override val text: String): Dictionary<String> {
    UNKNOWN("na", "неизвестно"),
    NO("wo", "жив, цел, орёл!"),
    LIGHT("l", "вроде цел"),
    HEAVY("h", "вроде жив"),
    LETHAL("d", "летальный");
}
