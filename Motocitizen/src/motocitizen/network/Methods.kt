package motocitizen.network

enum class Methods constructor(val code: String) {
    AUTH("auth"),
    GET_LIST("list"),
    BAN("ban"),
    ON_WAY("onway"),
    CANCEL_ON_WAY("cancelOnWay"),
    IN_PLACE("inplace"),
    LEAVE("leave"),
    MESSAGE("message"),
    CREATE("createAcc"),
    CHANGE_STATE("changeState");
}
