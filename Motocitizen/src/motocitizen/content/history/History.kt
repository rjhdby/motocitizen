package motocitizen.content.history

import motocitizen.dictionary.HistoryAction
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class History @Throws(JSONException::class)
constructor(json: JSONObject) {
    val ownerId = json.getInt("o")
    val time = Date(json.getLong("ut") * 1000)
    val action = HistoryAction.parse(json.getString("a"))
}
