package motocitizen.network

enum class Methods constructor(val code: String) {
    AUTH("auth"),//new
    LIST("getList"),//new
    BAN("ban"),
    DETAILS("getDetails"),//new
    ON_WAY("onway"),
    CANCEL_ON_WAY("cancelOnWay"),
    IN_PLACE("inplace"),
    LEAVE("leave"),
    MESSAGE("newMessage"), //new
    CREATE("createAcc"),
    CHANGE_STATE("changeState");
}
