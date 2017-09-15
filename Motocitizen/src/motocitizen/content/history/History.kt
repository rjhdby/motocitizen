package motocitizen.content.history

import motocitizen.content.Content
import motocitizen.dictionary.HistoryAction
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class History @Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = Date(json.getLong("ut") * 1000)
    val action = HistoryAction.parse(json.getString("a"))
    fun ownerName() = Content.volunteer(owner).name
}
