package motocitizen.content.history

import motocitizen.content.Content
import motocitizen.dictionary.HistoryAction
import motocitizen.utils.dateFromSeconds
import org.json.JSONException
import org.json.JSONObject

class History @Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = dateFromSeconds(json.getLong("ut"))
    val action = HistoryAction.parse(json.getString("a"))
    fun ownerName() = Content.volunteerName(owner)
}
