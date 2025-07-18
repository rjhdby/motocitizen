package motocitizen.dictionary

import com.fasterxml.jackson.annotation.JsonCreator

enum class AccidentStatus(override val code: String, override val text: String) : Dictionary<String> {
    ACTIVE("a", "Активный"),
    ENDED("e", "Отбой"),
    HIDDEN("h", "Скрыт");

    //    CONFLICT("w", "Конфликт"),
//    DUPLICATE("d", "Дубль");
    companion object {
        private val map = entries.associateBy { it.code }

        @Suppress("unused")
        @JsonCreator
        @JvmStatic
        fun fromString(code: String): AccidentStatus = map[code] ?: ACTIVE
    }
}