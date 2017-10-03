package motocitizen.content.volunteer

import motocitizen.content.Content
import motocitizen.dictionary.VolunteerActions
import motocitizen.utils.dateFromSeconds
import org.json.JSONException
import org.json.JSONObject

class VolunteerAction
@Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = dateFromSeconds(json.getLong("ut"))
    val status = VolunteerActions.parse(json.getString("s"))
    fun ownerName() = Content.volunteerName(owner)
}