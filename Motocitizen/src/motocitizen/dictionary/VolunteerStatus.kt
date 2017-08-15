package motocitizen.dictionary

enum class VolunteerStatus(private val code: String, val text: String) {
    ON_WAY("onway", "Выехал"),
    IN_PLACE("inplace", "На месте"),
    LEAVE("leave", "Уехал");

    companion object {
        fun parse(medicine: String): VolunteerStatus {
            return VolunteerStatus.values().firstOrNull { it.code == medicine }
                   ?: VolunteerStatus.ON_WAY
        }
    }
}
