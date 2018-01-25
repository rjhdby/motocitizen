package motocitizen.content

import motocitizen.content.message.Message
import motocitizen.utils.asList
import org.json.JSONArray
import java.util.*

object MessagesController {
    val messages: TreeMap<Int, Message> = TreeMap()
    fun addMessages(json: JSONArray) {
        json.asList()
                .map { Message(it) }
                .forEach { messages[it.id] = it }
    }
}