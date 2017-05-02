package motocitizen.content.volunteer

import motocitizen.dictionary.VolunteerStatus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Volunteer @Throws(JSONException::class)
constructor(json: JSONObject) {
    val id: Int = json.getInt("id")
    val name: String = json.getString("name")
    val time: Date = Date(json.getLong("uxtime") * 1000)
    val status: VolunteerStatus = VolunteerStatus.parse(json.getString("status"))
}
