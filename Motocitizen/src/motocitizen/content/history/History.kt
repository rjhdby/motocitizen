package motocitizen.content.history

import motocitizen.dictionary.HistoryAction
import motocitizen.utils.getEnumOr
import motocitizen.utils.getTimeFromSeconds
import org.json.JSONException
import org.json.JSONObject

class History @Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = json.getTimeFromSeconds()
    val action = json.getEnumOr("a", HistoryAction.OTHER)
}
