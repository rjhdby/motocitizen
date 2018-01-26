package motocitizen.content.accident

import motocitizen.utils.*
import org.json.JSONObject

object AccidentFactory {
    fun make(json: JSONObject): Accident = Accident(
            id = json.getInt("id"),
            status = json.getAccidentStatus(),
            type = json.getAccidentType(),
            medicine = json.getAccidentMedicine(),
            time = json.getTimeFromSeconds(),
            location = json.getAccidentLocation(),
            owner = json.getInt("o"))
            .apply {
                description = json.getString("d")
                messagesCount = json.getInt("mc")
            }
}