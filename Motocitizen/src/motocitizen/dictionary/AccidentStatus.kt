package motocitizen.dictionary

enum class AccidentStatus constructor(val code: String, val text: String) {
    /*
                            WHEN "acc_status_act" THEN "a"
                        WHEN "acc_status_dbl" THEN "d"
                        WHEN "acc_status_end"  THEN "e"
                        WHEN "acc_status_hide" THEN "h"
                        WHEN "acc_status_war" THEN "w"
     */
    ACTIVE("a", "Активный"),
    ENDED("e", "Отбой"),
    HIDDEN("h", "Скрыт"),
    CONFLICT("w", "Конфликт"),
    DUPLICATE("d", "Дубль");

    companion object {
        fun parse(status: String): AccidentStatus {
            return AccidentStatus.values().firstOrNull { it.code == status }
                   ?: AccidentStatus.ACTIVE
        }
    }
}