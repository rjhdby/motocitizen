package motocitizen.utils.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.dictionary.Medicine;
import motocitizen.user.User;
import motocitizen.utils.DateUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.Utils;
//todo renew after hide/end/activate

public class AccidentListPopup extends PopupWindowGeneral {
    private final Accident point;
    private final String   accText;

    public AccidentListPopup(Context context, int id) {
        super(context);
        point = Content.INSTANCE.accident(id);
        accText = getAccidentTextToCopy(point);
    }

    public PopupWindow getPopupWindow(Context context) {
        content.addView(copyButtonRow(context, accText));
        for (String phone : Utils.getPhonesFromText(point.getDescription())) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
            content.addView(smsButtonRow(context, phone), layoutParams);
        }
        if (User.INSTANCE.isModerator() || Preferences.INSTANCE.getLogin().equals(point.ownerName()))
            content.addView(finishButtonRow(point));

        if (User.INSTANCE.isModerator()) {
            content.addView(hideButtonRow(point));
            content.addView(banButtonRow(context, point.getOwner()), layoutParams);
        }

        content.addView(shareMessage(context, accText));
        content.addView(coordinatesButtonRow(context, point), layoutParams);
        popupWindow.setContentView(content);
        return popupWindow;
    }

    public static String getAccidentTextToCopy(Accident accident) {
        StringBuilder res = new StringBuilder();
        res.append(DateUtils.getDateTime(accident.getTime())).append(" ");
        res.append(accident.ownerName()).append(": ");
        res.append(accident.getType().getText()).append(". ");
        if (accident.getMedicine() != Medicine.UNKNOWN) {
            res.append(accident.getMedicine().getText()).append(". ");
        }
        res.append(accident.getAddress()).append(". ");
        res.append(accident.getDescription()).append(".");
        return res.toString();
    }
}
