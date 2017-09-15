package motocitizen.content

import motocitizen.content.message.Message
import org.json.JSONArray
import java.util.*

object MessagesController {
    val messages: TreeMap<Int, Message> = TreeMap()
    fun addMessages(json: JSONArray) {
        (0 until json.length())
                .map { Message(json.getJSONObject(it)) }
                .forEach { messages.put(it.id, it) }
    }
}