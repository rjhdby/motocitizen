package motocitizen.content.message

import org.json.JSONObject
import java.util.*

class MessageFactory {
    companion object {
        fun make(json: JSONObject): Message {
            val id = json.getInt("id")
            val owner = json.getInt("id_user")
            val text = json.getString("text")
            val time = Date(json.getLong("uxtime") * 1000)

            return Message(id, text, time, owner)
        }
    }
}
