package motocitizen.rows.message

import android.annotation.SuppressLint
import android.content.Context
import motocitizen.content.message.Message
import motocitizen.main.R

@SuppressLint("ViewConstructor")
class OwnMessageRow(context: Context, message: Message, last: Int, next: Int) : MessageRow(context, message, last, next) {
    override val FIRST = R.drawable.owner_message_row_first
    override val SOLO = R.drawable.owner_message_row
}
