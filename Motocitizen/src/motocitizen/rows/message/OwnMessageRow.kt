package motocitizen.rows.message

import android.content.Context
import motocitizen.content.message.Message
import motocitizen.main.R

class OwnMessageRow(context: Context, message: Message, last: Int, next: Int) : MessageRow(context, message, last, next) {
    override val LAYOUT = R.layout.owner_message_row
    override val FIRST = R.drawable.owner_message_row_first
    override val SOLO = R.drawable.owner_message_row
}
