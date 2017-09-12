package motocitizen.rows.message

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import motocitizen.content.message.Message
import motocitizen.main.R

class CommonMessageRow(context: Context, message: Message, last: Int, next: Int) : MessageRow(context, message, last, next) {
    override val LAYOUT = R.layout.message_row
    override val SOLO = R.drawable.message_row
    override val FIRST = R.drawable.owner_message_row_first
}
