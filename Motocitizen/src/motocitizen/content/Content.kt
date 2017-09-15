package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import org.json.JSONArray
import org.json.JSONObject

object Content {
    var inPlace: Int = 0 //todo

    fun accident(id: Int): Accident = AccidentsController.accidents[id]!! //TODO can produce NPE
    fun volunteer(id: Int): Volunteer = VolunteersController.volunteers[id]!!//TODO can produce NPE
    fun message(id: Int): Message = MessagesController.messages[id]!!//TODO can produce NPE

    fun requestUpdate(callback: (JSONObject) -> Unit) {
        AccidentsController.update(callback)
    }

    fun requestSingleAccident(id: Int, callback: (JSONObject) -> Unit) {
        AccidentsController.requestSingleAccident(id, callback)
    }

    fun requestDetailsForAccident(accident: Accident, callback: (JSONObject) -> Unit) {
        AccidentsController.requestDetailsForAccident(accident, callback)
    }

    fun addVolunteers(json: JSONObject) {
        VolunteersController.addVolunteers(json)
    }

    fun addMessages(json: JSONArray) {
        MessagesController.addMessages(json)
    }

    fun getVisible() = AccidentsController.accidents.values.filter { it.isVisible() }.toList()

    fun getVisibleReversed() = getVisible().sortedByDescending { it.id }

    fun getIds() = AccidentsController.accidents.keys

    fun messagesForAccident(accident: Accident) = MessagesController.messages.values.filter { it.accidentId == accident.id }
}
