package motocitizen.content.message

import motocitizen.user.User
import java.util.*

open class Message(val id: Int, val text: String, val time: Date, val owner: Int) {
    var read = owner == User.dirtyRead().id

    val isOwner: Boolean
        get() = owner == User.dirtyRead().id
}
