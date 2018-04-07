package motocitizen.content.volunteer

import motocitizen.dictionary.VolunteerActions
import motocitizen.utils.getEnumOr
import motocitizen.utils.getTimeFromSeconds
import org.json.JSONException
import org.json.JSONObject

class VolunteerAction
@Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = json.getTimeFromSeconds()
    val status = json.getEnumOr("s", VolunteerActions.ON_WAY)
}