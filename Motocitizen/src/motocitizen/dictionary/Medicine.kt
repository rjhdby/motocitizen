package motocitizen.dictionary

enum class Medicine constructor(val code: String, val text: String) {
    UNKNOWN("na", "неизвестно"),
    NO("wo", "жив, цел, орёл!"),
    LIGHT("l", "вроде цел"),
    HEAVY("h", "вроде жив"),
    LETHAL("d", "летальный");

    companion object {
        fun parse(medicine: String): Medicine {
            return Medicine.values().firstOrNull { it.code == medicine }
                   ?: Medicine.UNKNOWN
        }
    }
}
