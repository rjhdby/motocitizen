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

    public AccidentListPopup(Context context, int id) {
        super(context);
        point = Content.getPoint(id);
        accText = getAccidentTextToCopy(point);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(context, accText));
        for (String phone : MyUtils.getPhonesFromText(point.getDescription())) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
            content.addView(smsButtonRow(context, phone), layoutParams);
        }
        if (Content.auth.getRole().isModerator() || Content.auth.getLogin().equals(point.getOwner()))
            content.addView(finishButtonRow(context, point));

        if (Content.auth.getRole().isModerator()) {
            content.addView(hideButtonRow(context, point));
            content.addView(banButtonRow(point.getId()), layoutParams);
        }

        content.addView(shareMessage(context, accText));
        content.addView(coordinatesButtonRow(context, point), layoutParams);
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
