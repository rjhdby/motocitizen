package motocitizen.utils.popups

import android.content.Context
import android.widget.PopupWindow

import motocitizen.content.Content
import motocitizen.content.message.Message

import motocitizen.utils.getPhonesFromText

class MessagesPopup(context: Context, id: Int, accId: Int) : PopupWindowGeneral(context) {
    private val message: Message = Content.accidents[accId]!!.messages[id]

    fun getPopupWindow(context: Context): PopupWindow {
        content.addView(copyButtonRow(context, message.owner.toString() + ": " + message.text), layoutParams)
        for (phone in getPhonesFromText(message.text)) {
            content.addView(phoneButtonRow(context, phone), layoutParams)
        }
        popupWindow.contentView = content
        return popupWindow
    }
}
