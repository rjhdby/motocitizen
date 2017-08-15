package motocitizen.dictionary

enum class Medicine constructor(val code: String, val text: String) {
    /*
    WHEN "mc_m_d" THEN "d"
                        WHEN "mc_m_h" THEN "h"
                        WHEN "mc_m_l" THEN "l"
                        WHEN "mc_m_wo" THEN "wo"
                        ELSE "na"
     */
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
