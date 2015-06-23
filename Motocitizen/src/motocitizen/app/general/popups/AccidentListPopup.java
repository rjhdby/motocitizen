package motocitizen.app.general.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.user.Role;
import motocitizen.utils.MyUtils;

public class AccidentListPopup extends PopupWindowGeneral {
    Accident point;
    boolean  disableOldItems;
    String   accText;

    public AccidentListPopup(Context context, int id, boolean disableOldItems) {
        super(context);
        point = AccidentsGeneral.points.getPoint(id);
        this.disableOldItems = disableOldItems;
        accText = AccidentsGeneral.points.getTextToCopy(id);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(context, accText));
        if (!disableOldItems) {
            for (String phone : MyUtils.getPhonesFromText(point.getDescription())) {
                content.addView(phoneButtonRow(context, phone), layoutParams);
                content.addView(smsButtonRow(context, phone), layoutParams);
            }
        }
        if (Role.isModerator()||AccidentsGeneral.auth.getLogin().equals(point.getOwner())) {
            if (!disableOldItems) {
                content.addView(finishButtonRow(context, point));
            }
        }

        if (Role.isModerator()) {
            if (!disableOldItems) {
                content.addView(hideButtonRow(context, point));
            }
            content.addView(banButtonRow(context, point.getId()), layoutParams);
        }
        content.addView(shareMessage(context, accText));
        content.addView(coordinatesButtonRow(context, point), layoutParams);
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
