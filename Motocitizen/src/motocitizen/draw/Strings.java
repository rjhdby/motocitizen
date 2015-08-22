package motocitizen.draw;

import motocitizen.accident.Accident;
import motocitizen.content.Medicine;
import motocitizen.utils.Const;

/**
 * Created by U_60A9 on 14.08.2015.
 */
public class Strings {
    public static String getAccidentTextToCopy(Accident accident) {
        StringBuilder res = new StringBuilder();
        res.append(Const.dateFormat.format(accident.getTime())).append(". ");
        res.append(accident.getTypeString()).append(". ");
        if (accident.getMedicine() != Medicine.UNKNOWN) {
            res.append(accident.getMedicineString()).append(". ");
        }
        res.append(accident.getAddress()).append(". ");
        res.append(accident.getDescription()).append(".");
        return res.toString();
    }

}
