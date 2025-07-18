package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.volunteer.Volunteer
import motocitizen.datasources.network.response.AccidentListResponse
import motocitizen.datasources.network.response.AccidentResponse
import org.json.JSONArray

object Content {
    var inPlace: Accident? = null //todo

    operator fun get(index: Int) = AccidentsController.accidents[index]

    fun volunteerName(id: Int): String = VolunteersController.volunteers[id]?.name ?: ""  //todo

    inline fun getByFilter(filter: (Accident) -> Boolean): List<Accident> = AccidentsController.accidents.values.filter(filter)

    suspend fun requestUpdate(callback: (AccidentListResponse) -> Unit) = AccidentsController.loadAccidents(callback)

    suspend fun requestSingleAccident(id: Int, callback: (AccidentResponse) -> Unit) {
        AccidentsController.loadSingleAccident(id, callback)
    }

    fun addVolunteers(volunteers: List<Volunteer>) = VolunteersController.addVolunteers(volunteers)

    fun addMessages(json: JSONArray) = MessagesController.addMessages(json)

    fun getVisible() = AccidentsController.accidents.values.filter { it.isVisible() }.toList()

    fun getVisibleReversed() = getVisible().sortedByDescending { it.id }

    fun messagesForAccident(accident: Accident) = MessagesController.messages.values.filter { it.accidentId == accident.id }
}
