package motocitizen.dictionary

enum class VolunteerActions(val code: String, val text: String) {
    ON_WAY("onway", "Выехал"),
    IN_PLACE("inplace", "На месте"),
    LEAVE("leave", "Уехал");
}
