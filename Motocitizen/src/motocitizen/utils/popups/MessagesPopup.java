package motocitizen.utils.popups;

import android.content.Context;
import android.widget.PopupWindow;

import motocitizen.content.Content;
import motocitizen.content.message.Message;

import static motocitizen.utils.Utils.getPhonesFromText;

public class MessagesPopup extends PopupWindowGeneral {
    private final Message message;

    public MessagesPopup(Context context, int id, int accId) {
        super(context);
        message = Content.INSTANCE.getAccidents().get(accId).getMessages().get(id);
//        message = ContentLegacy.getInstance().get(accId).getMessages().get(id);
    }

    public PopupWindow getPopupWindow(Context context) {
        content.addView(copyButtonRow(context, message.getOwner() + ": " + message.getText()), layoutParams);
        for (String phone : getPhonesFromText(message.getText())) {
            content.addView(phoneButtonRow(context, phone), layoutParams);
        }
        popupWindow.setContentView(content);
        return popupWindow;
    }
}
