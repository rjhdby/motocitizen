package motocitizen.app.general.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.accident.Message;
import motocitizen.content.Content;
import motocitizen.utils.MyUtils;

public class MessagesPopup extends PopupWindowGeneral {
    private final Message message;

    public MessagesPopup(Context context, int id, int acc_id) {
        super(context);
        message = Content.getPoint(acc_id).getMessages().get(id);
    }

    public PopupWindow getPopupWindow() {
        content.addView(copyButtonRow(context, message.getOwner() + ": " + message.getText()), layoutParams);
        for (String phone : MyUtils.getPhonesFromText(message.getText())) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
        }
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
