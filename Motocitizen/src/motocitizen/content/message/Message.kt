package motocitizen.content.message

import motocitizen.user.User
import org.json.JSONException
import org.json.JSONObject
import java.util.*

open class Message(val id: Int, val text: String, val time: Date, val owner: Int) {
    var read = owner == User.id

    val isOwner: Boolean
        get() = owner == User.id

    @Throws(JSONException::class)
    constructor(json: JSONObject) : this(json.getInt("id"), json.getString("t"), Date(json.getLong("ut") * 1000), json.getInt("o"))
}
