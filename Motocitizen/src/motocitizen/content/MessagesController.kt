package motocitizen.content

import motocitizen.content.message.Message
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object MessagesController {
    val messages: TreeMap<Int, Message> = TreeMap()
    fun addMessages(json: JSONArray) {
        (json as List<JSONObject>)
                .map { Message(it) }
                .forEach { messages.put(it.id, it) }
    }
}