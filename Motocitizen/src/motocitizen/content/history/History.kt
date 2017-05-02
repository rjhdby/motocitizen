package motocitizen.content.history

import motocitizen.dictionary.HistoryAction
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class History @Throws(JSONException::class)
constructor(json: JSONObject) {
    val id = json.getInt("id")
    val ownerId = json.getInt("id_user")
    val owner = json.getString("owner")
    val time = Date(json.getLong("uxtime") * 1000)
    val action = HistoryAction.parse(json.getString("action"))
}
