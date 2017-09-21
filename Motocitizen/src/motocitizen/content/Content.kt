package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import motocitizen.datasources.network.ApiResponse
import org.json.JSONArray
import org.json.JSONObject

object Content {
    var inPlace: Accident? = null //todo

    fun accident(id: Int): Accident = AccidentsController.accidents[id]!! //TODO can produce NPE
    fun volunteer(id: Int): Volunteer = VolunteersController.volunteers[id]!!//TODO can produce NPE
    fun message(id: Int): Message = MessagesController.messages[id]!!//TODO can produce NPE

    fun getByFilter(filter: (Accident) -> Boolean): List<Accident> = AccidentsController.accidents.values.filter(filter)

    fun requestUpdate(callback: (ApiResponse) -> Unit) {
        AccidentsController.update(callback)
    }

    fun requestSingleAccident(id: Int, callback: (ApiResponse) -> Unit) {
        AccidentsController.requestSingleAccident(id, callback)
    }

    fun requestDetailsForAccident(accident: Accident, callback: (ApiResponse) -> Unit) {
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
