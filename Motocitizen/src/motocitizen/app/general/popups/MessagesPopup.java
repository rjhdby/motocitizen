package motocitizen.app.general.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.accident.Message;
import motocitizen.content.Content;
import motocitizen.utils.MyUtils;

public class MessagesPopup extends PopupWindowGeneral {
    private final Message message;

    public MessagesPopup(int id, int acc_id) {
        super();
        message = Content.getPoint(acc_id).getMessages().get(id);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(message.getOwner() + ": " + message.getText()), layoutParams);
        for (String phone : MyUtils.getPhonesFromText(message.getText())) {
            content.addView(phoneButtonRow(phone), layoutParams);
        }
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
