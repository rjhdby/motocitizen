package motocitizen.content;

public enum VolunteerStatus {
    ONWAY, INPLACE, LEAVE;

    public static final String leave   = "leave";
    public static final String inplace = "inplace";
    public static final String onway   = "onway";

    public static VolunteerStatus parse(String status) {
        switch (status) {
            case leave:
                return VolunteerStatus.LEAVE;
            case inplace:
                return VolunteerStatus.INPLACE;
            case onway:
            default:
                return VolunteerStatus.ONWAY;
        }
    }

    public String toCode() {
        return getCode(this);
    }

    public static String getCode(VolunteerStatus status) {
        switch (status) {
            case LEAVE:
                return leave;
            case INPLACE:
                return inplace;
            case ONWAY:
            default:
                return onway;
        }
    }

    @Override
    public String toString() {
        return getString(this);
    }

    public static String getString(VolunteerStatus status) {
        switch (status) {
            case INPLACE:
                return "На месте";
            case LEAVE:
                return "Уехал";
            case ONWAY:
            default:
                return "Выехал";
        }
    }
}
