package motocitizen.content.volunteer

import motocitizen.content.Content
import motocitizen.dictionary.VolunteerActions
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VolunteerAction
@Throws(JSONException::class)
constructor(json: JSONObject) {
    val owner = json.getInt("o")
    val time = Date(json.getLong("ut") * 1000)
    val status = VolunteerActions.parse(json.getString("s"))
    fun ownerName() = Content.volunteerName(owner)
}