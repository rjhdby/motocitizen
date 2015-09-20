package motocitizen.content;

public enum Medicine {
    UNKNOWN("mc_m_na", "неизвестно"),
    NO("mc_m_wo", "жив, цел, орёл!"),
    LIGHT("mc_m_l", "вроде цел"),
    HEAVY("mc_m_h", "вроде жив"),
    LETHAL("mc_m_d", "летальный");

    private final String code;
    private final String text;

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

    public String toCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
