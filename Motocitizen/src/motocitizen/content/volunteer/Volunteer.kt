package motocitizen.content.volunteer

import org.json.JSONException
import org.json.JSONObject

class Volunteer
@Throws(JSONException::class)
constructor(json: JSONObject) {
    val id = json.getInt("id")
    val name: String = json.getString("name")
}