package motocitizen.content.message

import android.content.Context
import motocitizen.rows.message.MessageRow
import motocitizen.rows.message.OwnMessageRow
import motocitizen.user.User
import java.util.*

class OwnedMessage(id: Int, text: String, time: Date) : Message(id, text, time, User.dirtyRead().id) {
    //class OwnedMessage(id: Int, text: String, time: Date) : Message(id, text, time, OwnerLegacy(User.dirtyRead().id, User.dirtyRead().name)) {
    override fun getRow(context: Context, last: Int, next: Int): MessageRow = OwnMessageRow(context, this, last, next)
}