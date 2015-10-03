package motocitizen.app.general.popups;

import android.widget.PopupWindow;

import motocitizen.MyApp;
import motocitizen.accident.Message;
import motocitizen.utils.MyUtils;

public class MessagesPopup extends PopupWindowGeneral {
    private final Message message;

    public MessagesPopup(int id, int acc_id) {
        super();
        message = MyApp.getContent().getPoint(acc_id).getMessages().get(id);
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
