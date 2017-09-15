package motocitizen.content.message

import motocitizen.content.Content
import motocitizen.user.User
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Message @Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val text: String = json.getString("t")
    val id = json.getInt("id")
    val time = Date(json.getLong("ut") * 1000)
    val accidentId = json.getInt("a")
    val isOwner: Boolean
        get() = owner == User.id

    fun ownerName() = Content.volunteer(owner).name
}
