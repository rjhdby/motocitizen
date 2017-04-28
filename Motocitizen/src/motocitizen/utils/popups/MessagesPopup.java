package motocitizen.utils.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.content.Message;
import motocitizen.dictionary.Content;
import motocitizen.utils.MyUtils;

public class MessagesPopup extends PopupWindowGeneral {
    private final Message message;

    public MessagesPopup(Context context, int id, int accId) {
        super(context);
        message = Content.getInstance().get(accId).getMessages().get(id);
    }

    public PopupWindow getPopupWindow(Context context) {
        content.addView(copyButtonRow(context, message.getOwner() + ": " + message.getText()), layoutParams);
        for (String phone : MyUtils.getPhonesFromText(message.getText())) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
        }
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
