package motocitizen.ui.menus

import android.content.Context
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.copyToClipBoard
import motocitizen.utils.getPhonesFromText
import motocitizen.utils.makeDial
import motocitizen.utils.name
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

class MessageContextMenu(context: Context, val message: Message) : ContextMenu(context) {
    init {
        addButton(R.string.share) { context.share(message.text) }
        addButton(R.string.copy) { context.copyToClipBoard(message.owner.name() + ":" + message.text) }
        message.text.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_dial, it)) { context.makeDial(it) }
        }
        message.text.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_sms, it)) { context.sendSMS(it) }
        }
    }
}