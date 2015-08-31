package motocitizen.content;

public enum Type {
    BREAK, SOLO, MOTO_MOTO, MOTO_AUTO, MOTO_MAN, OTHER, STEAL, USER;

    public static final String moto_break = "acc_b";
    public static final String solo       = "acc_m";
    public static final String moto_auto  = "acc_m_a";
    public static final String moto_moto  = "acc_m_m";
    public static final String moto_man   = "acc_m_p";
    public static final String steal      = "acc_s";
    public static final String other      = "acc_o";

    public static Type parse(String type) {
        switch (type) {
            case moto_break:
                return BREAK;
            case solo:
                return SOLO;
            case moto_auto:
                return MOTO_AUTO;
            case moto_moto:
                return MOTO_MOTO;
            case moto_man:
                return MOTO_MAN;
            case steal:
                return STEAL;
            case other:
            default:
                return OTHER;
        }
    }

    public String toCode() {
        return getTypeCode(this);
    }

    public static String getTypeCode(Type type) {
        switch (type) {
            case MOTO_AUTO:
                return moto_auto;
            case MOTO_MOTO:
                return moto_moto;
            case MOTO_MAN:
                return moto_man;
            case STEAL:
                return steal;
            case BREAK:
                return moto_break;
            case SOLO:
                return solo;
            case OTHER:
            default:
                return other;
        }
    }

    @Override
    public String toString() {
        return getTypeString(this);
    }

    public static String getTypeString(Type type) {
        switch (type) {
            case MOTO_AUTO:
                return "Мот/авто";
            case MOTO_MOTO:
                return "Мот/мот";
            case MOTO_MAN:
                return "Наезд на пешехода";
            case STEAL:
                return "Угон";
            case BREAK:
                return "Поломка";
            case SOLO:
                return "Один участник";
            case OTHER:
            default:
                return "Прочее";
        }
    }
}
