package motocitizen.content.message

import motocitizen.user.User
import org.json.JSONObject
import java.util.*

class MessageFactory {
    companion object {
        fun make(json: JSONObject): Message {
            val id = json.getInt("id")
            val owner = json.getInt("id_user")
//            val owner = Owner(json.getInt("id_user"), json.getString("owner"))
            val text = json.getString("text")
            val time = Date(json.getLong("uxtime") * 1000)

            return when (owner) {
                User.dirtyRead().id -> OwnedMessage(id, text, time)
                else                -> Message(id, text, time, owner)
            }
        }
    }
}