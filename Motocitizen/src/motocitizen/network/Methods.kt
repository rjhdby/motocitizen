package motocitizen.network

enum class Methods constructor(val code: String) {
    AUTH("auth"),//new
    LIST("getList"),//new
    BAN("ban"),
    DETAILS("getDetails"),//new
    ON_WAY("onway"),//new
    CANCEL_ON_WAY("cancel"),//new
    IN_PLACE("inPlace"),//new
    LEAVE("leave"),//new
    MESSAGE("newMessage"), //new
    CREATE("createAcc"),
    ACTIVATE_ACCIDENT("activateAccident"),//new
    END_ACCIDENT("endAccident"),//new
    HIDE_ACCIDENT("hideAccident");//new
//    ,
//    CHANGE_STATE("changeState");
}
