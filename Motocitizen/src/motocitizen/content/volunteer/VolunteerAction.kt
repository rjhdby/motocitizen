package motocitizen.content.volunteer

import motocitizen.utils.getTimeFromSeconds
import motocitizen.utils.getVolunteerAction
import org.json.JSONException
import org.json.JSONObject

class VolunteerAction
@Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = json.getTimeFromSeconds()
    val status = json.getVolunteerAction()
}