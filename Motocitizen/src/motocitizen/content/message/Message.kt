package motocitizen.content.message

import motocitizen.user.User
import org.json.JSONException
import org.json.JSONObject
import java.util.*

open class Message @Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val text: String = json.getString("t")
    val id = json.getInt("id")
    val time = Date(json.getLong("ut") * 1000)
    var read = owner == User.id //todo exterminatus
    val isOwner: Boolean
        get() = owner == User.id
}
