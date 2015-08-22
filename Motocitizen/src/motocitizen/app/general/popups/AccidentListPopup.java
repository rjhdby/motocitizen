package motocitizen.app.general.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.content.Content;
import motocitizen.app.general.user.Role;
import motocitizen.draw.Strings;
import motocitizen.utils.MyUtils;

public class AccidentListPopup extends PopupWindowGeneral {
    private final motocitizen.accident.Accident point;
    private final boolean                       disableOldItems;
    private final String                        accText;

    public AccidentListPopup(Context context, int id, boolean disableOldItems) {
        super(context);
        point = Content.getPoint(id);
        this.disableOldItems = disableOldItems;
        accText = Strings.getAccidentTextToCopy(point);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(context, accText));
        if (!disableOldItems) {
            for (String phone : MyUtils.getPhonesFromText(point.getDescription())) {
                content.addView(phoneButtonRow(context, phone), layoutParams);
                content.addView(smsButtonRow(context, phone), layoutParams);
            }
        }
        if (Role.isModerator()|| Content.auth.getLogin().equals(point.getOwner())) {
            if (!disableOldItems) {
                content.addView(finishButtonRow(context, point));
            }
        }

        if (Role.isModerator()) {
            if (!disableOldItems) {
                content.addView(hideButtonRow(context, point));
            }
            content.addView(banButtonRow(point.getId()), layoutParams);
        }
        content.addView(shareMessage(context, accText));
        content.addView(coordinatesButtonRow(context, point), layoutParams);
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
