package motocitizen.content;

public enum Type {
    BREAK("acc_b", "Поломка"),
    SOLO("acc_m", "Один участник"),
    MOTO_MOTO("acc_m_m", "Мот/мот"),
    MOTO_AUTO("acc_m_a", "Мот/авто"),
    MOTO_MAN("acc_m_p", "Наезд на пешехода"),
    OTHER("acc_o", "Прочее"),
    STEAL("acc_s", "Угон"),
    USER("user", "Вы");

    private final String code;
    private final String text;

    Type(String code, String text) {
        this.text = text;
        this.code = code;
    }

    public static Type parse(String type) {
        for (Type a : Type.values()) {
            if (a.code.equals(type)) return a;
        }
        return Type.OTHER;
    }

    public String toCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
