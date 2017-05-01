package motocitizen.content.message

import android.content.Context
import motocitizen.rows.message.MessageRow
import motocitizen.rows.message.OwnMessageRow
import motocitizen.user.Owner
import motocitizen.user.User
import java.util.*

class OwnedMessage(id: Int, text: String, time: Date) : Message(id, text, time, Owner(User.getInstance().id, User.getInstance().name)) {
    override fun getRow(context: Context, last: Int, next: Int): MessageRow {
        return OwnMessageRow(context, this, last, next)
    }
}