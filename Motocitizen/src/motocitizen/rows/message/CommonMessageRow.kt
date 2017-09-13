package motocitizen.rows.message

import android.content.Context
import motocitizen.content.message.Message
import motocitizen.main.R

class CommonMessageRow(context: Context, message: Message, type: MessageRow.Companion.Type) : MessageRow(context, message, type) {
    override val LAYOUT = R.layout.message_row
    override val ONE = R.drawable.message_row
    override val FIRST = R.drawable.owner_message_row_first
}
