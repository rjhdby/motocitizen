package motocitizen.content.volunteer

import motocitizen.dictionary.VolunteerStatus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Volunteer(val id: Int, val name: String) {
    val status = VolunteerStatus.IN_PLACE//todo
    val time = Date()//todo
    @Throws(JSONException::class)
    constructor(json: JSONObject) : this(json.getInt("id"), json.getString("name")) {
//        val time: Date = Date(json.getLong("uxtime") * 1000)
//        val status: VolunteerStatus = VolunteerStatus.parse(json.getString("status"))
    }
}