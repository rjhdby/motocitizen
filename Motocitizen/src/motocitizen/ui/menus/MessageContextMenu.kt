package motocitizen.ui.menus

import android.content.Context
import android.content.Intent
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.copyToClipBoard
import motocitizen.utils.getPhonesFromText
import motocitizen.utils.makeDial
import motocitizen.utils.name
import androidx.core.net.toUri

class MessageContextMenu(context: Context, val message: Message) : ContextMenu(context) {
    init {
        addButton(R.string.share) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message.text)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share)))
        }
        addButton(R.string.copy) { context.copyToClipBoard(message.owner.name() + ":" + message.text) }
        message.text.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_dial, it)) { context.makeDial(it) }
        }
        message.text.getPhonesFromText().forEach { phone ->
            addButton(context.getString(R.string.popup_sms, phone)) {
                val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "smsto:$phone".toUri()
                }
                context.startActivity(smsIntent)
            }
        }
    }
}