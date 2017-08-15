package motocitizen.utils.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.content.NewContent;
import motocitizen.content.accident.Accident;
import motocitizen.dictionary.Medicine;
import motocitizen.user.User;
import motocitizen.utils.DateUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.Utils;

public class AccidentListPopup extends PopupWindowGeneral {
    private final Accident point;
    private final String   accText;

    public AccidentListPopup(Context context, int id) {
        super(context);
        point = NewContent.INSTANCE.getAccidents().get(id);
//        point = Content.getInstance().get(id);
        accText = getAccidentTextToCopy(point);
    }

    public PopupWindow getPopupWindow(Context context) {
        content.addView(copyButtonRow(context, accText));
        for (String phone : Utils.getPhonesFromText(point.getDescription())) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
            content.addView(smsButtonRow(context, phone), layoutParams);
        }
        if (User.getInstance(context).isModerator() || Preferences.Companion.getInstance(context).getLogin().equals(NewContent.INSTANCE.getVolunteers().get(point.getOwner()).getName()))
            content.addView(finishButtonRow(point));

        if (User.getInstance(context).isModerator()) {
            content.addView(hideButtonRow(point));
            content.addView(banButtonRow(context, point.getId()), layoutParams);
        }

        content.addView(shareMessage(context, accText));
        content.addView(coordinatesButtonRow(context, point), layoutParams);
        popupWindow.setContentView(content);
        return popupWindow;
    }

    public static String getAccidentTextToCopy(Accident accident) {
        StringBuilder res = new StringBuilder();
        res.append(DateUtils.getDateTime(accident.getTime())).append(" ");
        res.append(NewContent.INSTANCE.getVolunteers().get(accident.getOwner()).getName()).append(": ");
        res.append(accident.getType().string()).append(". ");
        if (accident.getMedicine() != Medicine.UNKNOWN) {
            res.append(accident.getMedicine().string()).append(". ");
        }
        res.append(accident.getAddress()).append(". ");
        res.append(accident.getDescription()).append(".");
        return res.toString();
    }
}
