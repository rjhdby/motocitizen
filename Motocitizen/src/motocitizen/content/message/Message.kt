package motocitizen.content.message

import android.content.Context
import motocitizen.rows.message.MessageRow
import motocitizen.user.Owner
import java.util.*

open class Message(val id: Int, val text: String, val time: Date, val owner: Owner) {
    open fun getRow(context: Context, last: Int, next: Int): MessageRow {
        return MessageRow(context, this, last, next)
    }

    var read = owner.isUser

    val isOwner: Boolean
        get() = owner.isUser
}
