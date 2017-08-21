package motocitizen.content.volunteer

import motocitizen.dictionary.VolunteerActions
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VolunteerAction(val id: Int, val time: Date, val status: VolunteerActions) {
    @Throws(JSONException::class)
    constructor(json: JSONObject) : this(json.getInt("o"), Date(json.getLong("ut") * 1000), VolunteerActions.parse(json.getString("s")))
}