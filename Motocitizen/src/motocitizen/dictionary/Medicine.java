package motocitizen.dictionary;

public enum Medicine {
    UNKNOWN("mc_m_na", "неизвестно"),
    NO("mc_m_wo", "жив, цел, орёл!"),
    LIGHT("mc_m_l", "вроде цел"),
    HEAVY("mc_m_h", "вроде жив"),
    LETHAL("mc_m_d", "летальный");

    public final String code;
    public final String text;

    Medicine(String code, String text) {
        this.text = text;
        this.code = code;
    }

    public static Medicine parse(String medicine) {
        for (Medicine a : Medicine.values()) {
            if (a.code.equals(medicine)) return a;
        }
        return Medicine.UNKNOWN;
    }

    public String code() {
        return this.code;
    }

    public String string() {return text;}

    @Override
    public String toString() {
        return text;
    }
}
