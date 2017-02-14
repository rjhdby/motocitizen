package motocitizen.content;

public enum VolunteerStatus {
    ON_WAY("onway", "Выехал"),
    IN_PLACE("inplace", "На месте"),
    LEAVE("leave", "Уехал");

    private final String code;
    private final String text;

    VolunteerStatus(String code, String text) {
        this.text = text;
        this.code = code;
    }

    public static VolunteerStatus parse(String medicine) {
        for (VolunteerStatus a : VolunteerStatus.values()) {
            if (a.code.equals(medicine)) return a;
        }
        return VolunteerStatus.ON_WAY;
    }

    public String code() {
        return this.code;
    }

    public String string() {
        return this.text;
    }
}
