package motocitizen.ui.popups

import android.content.Context

import motocitizen.content.Content
import motocitizen.content.message.Message

import motocitizen.utils.getPhonesFromText

class MessagesPopup(context: Context, id: Int) : PopupWindowGeneral(context) {
    private val message: Message = Content.message(id)
    init {
        rootView.addView(copyButtonView(message.owner.toString() + ": " + message.text), layoutParams)
        for (phone in getPhonesFromText(message.text)) {
            rootView.addView(phoneButtonView(phone), layoutParams)
        }
        contentView = rootView
    }
}
