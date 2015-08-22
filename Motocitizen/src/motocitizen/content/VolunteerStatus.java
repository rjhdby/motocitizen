package motocitizen.content;

public enum VolunteerStatus {
    ONWAY, INPLACE, LEAVE;

    public static final String leave   = "leave";
    public static final String inplace = "inplace";
    public static final String onway = "onway";

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
}
