package motocitizen.network;

public enum Methods {
    AUTH("auth"),
    GET_LIST("list"),
    BAN("ban"),
    ON_WAY("onway"),
    CANCEL_ON_WAY("cancelOnWay"),
    IN_PLACE("inplace"),
    LEAVE("leave"),
    MESSAGE("message"),
    CREATE("createAcc"),
    REGISTER_GCM("registerGCM"),
    CHANGE_STATE("changeState");

    private final String code;

    Methods(String code) {
        this.code = code;
    }

    public String toCode() {
        return this.code;
    }
}
