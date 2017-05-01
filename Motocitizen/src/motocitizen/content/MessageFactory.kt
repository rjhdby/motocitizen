package motocitizen.content

import motocitizen.content.message.Message
import motocitizen.content.message.OwnedMessage
import motocitizen.user.Owner
import org.json.JSONObject
import java.util.*

class MessageFactory {
    companion object {
        fun make(json: JSONObject): Message {
            val id = json.getInt("id")
            val owner = Owner(json.getInt("id_user"), json.getString("owner"))
            val text = json.getString("text")
            val time = Date(json.getLong("uxtime") * 1000)

            return when {
                owner.isUser -> OwnedMessage(id, text, time)
                else         -> Message(id, text, time, owner)
            }
        }
    }
}