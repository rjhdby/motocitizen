package motocitizen.rows.message

import android.content.Context
import motocitizen.content.message.Message

object MessageRowFactory {
    fun make(context: Context, message: Message, last: Int, next: Int): MessageRow =
            if (message.isOwner) OwnMessageRow(context, message, last, next) else MessageRow(context, message, last, next)
}