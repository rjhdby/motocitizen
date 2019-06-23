package motocitizen.ui.rows.message

import android.content.Context
import motocitizen.content.message.Message
import motocitizen.main.R

class OwnMessageRow(context: Context, message: Message, type: Type) : MessageRow(context, message, type) {
    override val FIRST = R.drawable.owner_message_row_first
    override val ONE = R.drawable.owner_message_row
}
