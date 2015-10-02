package motocitizen.app.general.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.accident.Accident;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;

public class AccidentListPopup extends PopupWindowGeneral {
    private final Accident point;
    private final String   accText;

    public AccidentListPopup(int id) {
        super();
        point = Content.getPoint(id);
        accText = getAccidentTextToCopy(point);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(accText));
        for (String phone : MyUtils.getPhonesFromText(point.getDescription())) {
            content.addView(phoneButtonRow(phone), layoutParams);
            content.addView(smsButtonRow(phone), layoutParams);
        }
        if (Content.auth.getRole().isModerator() || Content.auth.getLogin().equals(point.getOwner()))
            content.addView(finishButtonRow(point));

        if (Content.auth.getRole().isModerator()) {
            content.addView(hideButtonRow(point));
            content.addView(banButtonRow(point.getId()), layoutParams);
        }

        content.addView(shareMessage(accText));
        content.addView(coordinatesButtonRow(point), layoutParams);
        popupWindow.setContentView(content);
        return popupWindow;
    }
    public static String getAccidentTextToCopy(Accident accident) {
        StringBuilder res = new StringBuilder();
        res.append(Const.DATE_FORMAT.format(accident.getTime())).append(" ");
        res.append(accident.getOwner()).append( ": " );
        res.append(accident.getType().toString()).append(". ");
        if (accident.getMedicine() != Medicine.UNKNOWN) {
            res.append(accident.getMedicine().toString()).append(". ");
        }
        res.append(accident.getAddress()).append(". ");
        res.append(accident.getDescription()).append(".");
        return res.toString();
    }
}
