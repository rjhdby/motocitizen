package motocitizen.dictionary

enum class VolunteerActions(private val code: String, val text: String) {
    ON_WAY("onway", "Выехал"),
    IN_PLACE("inplace", "На месте"),
    LEAVE("leave", "Уехал");

    companion object {
        fun parse(medicine: String): VolunteerActions {
            return VolunteerActions.values().firstOrNull { it.code == medicine }
                   ?: VolunteerActions.ON_WAY
        }
    }
}
