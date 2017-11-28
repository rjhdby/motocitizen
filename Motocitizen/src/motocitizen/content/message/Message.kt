package motocitizen.content.message

import motocitizen.user.User
import motocitizen.utils.getTimeFromSeconds
import org.json.JSONException
import org.json.JSONObject

class Message @Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val text: String = json.getString("t")
    val id = json.getInt("id")
    val time = json.getTimeFromSeconds()
    val accidentId = json.getInt("a")
    val isOwner: Boolean
        inline get() = owner == User.id
}
