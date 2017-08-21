package motocitizen.content.volunteer

import org.json.JSONException
import org.json.JSONObject

class Volunteer(val id: Int, val name: String) {
    @Throws(JSONException::class)
    constructor(json: JSONObject) : this(json.getInt("id"), json.getString("name"))
}