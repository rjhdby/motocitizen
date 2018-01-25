package motocitizen.datasources.network

enum class Methods constructor(val code: String) {
    AUTH("auth"),
    LIST("getList"),
    BAN("ban"),
    DETAILS("getDetails"),
    ON_WAY("onWay"),
    CANCEL_ON_WAY("cancel"),
    IN_PLACE("inPlace"),
    LEAVE("leave"),
    MESSAGE("newMessage"),
    CREATE("createAccident"),
    ACTIVATE_ACCIDENT("activateAccident"),
    END_ACCIDENT("endAccident"),
    HAS_NEW("hasNew"),
    HIDE_ACCIDENT("hideAccident"),
    ACCIDENT("getAccident")
}
