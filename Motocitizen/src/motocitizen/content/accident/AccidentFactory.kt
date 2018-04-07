package motocitizen.content.accident

import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.utils.getAccidentLocation
import motocitizen.utils.getEnumOr
import motocitizen.utils.getTimeFromSeconds
import org.json.JSONObject

object AccidentFactory {
    fun make(json: JSONObject): Accident = Accident(
            id = json.getInt("id"),
            status = json.getEnumOr("s", AccidentStatus.ACTIVE),
            type = json.getEnumOr("t", Type.OTHER),
            medicine = json.getEnumOr("m", Medicine.UNKNOWN),
            time = json.getTimeFromSeconds(),
            location = json.getAccidentLocation(),
            owner = json.getInt("o"))
            .apply {
                description = json.getString("d")
                messagesCount = json.getInt("mc")
            }
}