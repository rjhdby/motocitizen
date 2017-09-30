package motocitizen.ui.rows.message

import android.content.Context
import motocitizen.content.message.Message
import motocitizen.main.R

class CommonMessageRow(context: Context, message: Message, type: MessageRow.Type) : MessageRow(context, message, type) {
    override val ONE = R.drawable.message_row
    override val FIRST = R.drawable.message_row_first
}
