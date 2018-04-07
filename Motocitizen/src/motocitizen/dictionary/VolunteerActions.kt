package motocitizen.dictionary

enum class VolunteerActions(override val code: String, override val text: String) : Dictionary<String> {
    ON_WAY("onway", "Выехал"),
    IN_PLACE("inplace", "На месте"),
    LEAVE("leave", "Уехал");
}
