package motocitizen.content;

public enum Medicine {
    UNKNOWN, NO, LIGHT, HEAVY, LETHAL;

    public static final String lethal  = "mc_m_d";
    public static final String no      = "mc_m_wo";
    public static final String light   = "mc_m_l";
    public static final String heavy   = "mc_m_h";
    public static final String unknown = "mc_m_na";

    public static Medicine parse(String medicine) {
        switch (medicine) {
            case lethal:
                return LETHAL;
            case no:
                return NO;
            case light:
                return LIGHT;
            case heavy:
                return HEAVY;
            case unknown:
            default:
                return UNKNOWN;
        }
    }

    public String toCode(){
        return getCode(this);
    }

    public static String getCode(Medicine medicine) {
        switch (medicine) {
            case LETHAL:
                return lethal;
            case HEAVY:
                return heavy;
            case LIGHT:
                return light;
            case NO:
                return no;
            case UNKNOWN:
            default:
                return unknown;
        }
    }

    @Override
    public String toString() {
        return getMedicineString(this);
    }

    public static String getMedicineString(Medicine medicine) {
        switch (medicine) {
            case LETHAL:
                return "летальный";
            case HEAVY:
                return "тяжелый";
            case LIGHT:
                return "ушибы";
            case NO:
                return "жив, цел, орёл!";
            case UNKNOWN:
            default:
                return "неизвестно";
        }
    }
}
