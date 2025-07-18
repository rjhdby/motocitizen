package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.response.AccidentListResponse
import motocitizen.datasources.network.response.AccidentResponse
import motocitizen.datasources.preferences.Preferences
import motocitizen.utils.getRequest
import motocitizen.utils.httpClient
import motocitizen.utils.seconds
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

object AccidentsController {
    private var lastUpdate: Long = 0
    val accidents: ConcurrentHashMap<Int, Accident> = ConcurrentHashMap()

    fun resetLastUpdate() {
        lastUpdate = 0
    }

    suspend fun loadAccidents(callback: (AccidentListResponse) -> Unit) {
        httpClient.getRequest<AccidentListResponse>("a" to Preferences.hoursAgo) {
            lastUpdate = Date().seconds()
            Content.addVolunteers(it.getVolunteers())
            addAccidents(it.accidents.map { acc -> acc.toAccident() })
            callback(it)
        }
    }

    suspend fun loadSingleAccident(id: Int, callback: (AccidentResponse) -> Unit) {
        httpClient.getRequest<AccidentResponse>("id" to id) {
            callback(it)
        }
    }

    private fun addAccidents(newAccidents: List<Accident>) {
        newAccidents.forEach { accidents[it.id] = it }
    }
}