package motocitizen.dictionary;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import motocitizen.main.R;

public enum Type {
    /*
                            WHEN "acc_b" THEN "b"
                        WHEN "acc_m" THEN "m"
                        WHEN "acc_m_a" THEN "ma"
                        WHEN "acc_m_m" THEN "mm"
                        WHEN "acc_m_p" THEN "mp"
                        WHEN "acc_s" THEN "s"
                        ELSE "o"
     */
    BREAK("b", "Поломка", R.drawable.break_icon),
    SOLO("m", "Один участник", R.drawable.accident),
    MOTO_MOTO("mm", "Мот/мот", R.drawable.accident),
    MOTO_AUTO("ma", "Мот/авто", R.drawable.accident),
    MOTO_MAN("mp", "Наезд на пешехода", R.drawable.accident),
    OTHER("o", "Прочее", R.drawable.other),
    STEAL("s", "Угон", R.drawable.other),
    USER("user", "Вы", R.drawable.osm_moto_icon);
//    BREAK("acc_b", "Поломка", R.drawable.break_icon),
//    SOLO("acc_m", "Один участник", R.drawable.accident),
//    MOTO_MOTO("acc_m_m", "Мот/мот", R.drawable.accident),
//    MOTO_AUTO("acc_m_a", "Мот/авто", R.drawable.accident),
//    MOTO_MAN("acc_m_p", "Наезд на пешехода", R.drawable.accident),
//    OTHER("acc_o", "Прочее", R.drawable.other),
//    STEAL("acc_s", "Угон", R.drawable.other),
//    USER("user", "Вы", R.drawable.osm_moto_icon);

    private final String code;
    private final String text;
    private final int    mapIcon;

    Type(String code, String text, int mapIcon) {
        this.text = text;
        this.code = code;
        this.mapIcon = mapIcon;
    }

    public static Type parse(String type) {
        for (Type a : Type.values()) {
            if (a.code.equals(type)) return a;
        }
        return Type.OTHER;
    }

    public String code() {
        return this.code;
    }

    public String string() {
        return this.text;
    }

    public BitmapDescriptor getIcon() {
        return BitmapDescriptorFactory.fromResource(this.mapIcon);
    }

    @Override
    public String toString() {
        return text;
    }
}
