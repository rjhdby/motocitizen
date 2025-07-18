package motocitizen.dictionary

import com.fasterxml.jackson.annotation.JsonCreator

enum class Medicine(override val code: String, override val text: String) : Dictionary<String> {
    UNKNOWN("na", "неизвестно"),
    NO("wo", "жив, цел, орёл!"),
    LIGHT("l", "вроде цел"),
    HEAVY("h", "вроде жив"),
    LETHAL("d", "летальный");

    companion object {
        private val map = entries.associate { it.code to it }

        @Suppress("unused")
        @JsonCreator
        @JvmStatic
        fun fromString(code: String): Medicine = map[code] ?: UNKNOWN
    }
}
