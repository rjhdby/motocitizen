package motocitizen.app.general.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.app.general.AccidentMessage;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.utils.MyUtils;

public class MessagesPopup extends PopupWindowGeneral {
    private final AccidentMessage message;

    public MessagesPopup(Context context, int id, int acc_id) {
        super(context);
        message = AccidentsGeneral.points.getPoint(acc_id).messages.get(id);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(context, message.owner + ": " + message.text), layoutParams);
        for (String phone : MyUtils.getPhonesFromText(message.text)) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
        }
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
