package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.accident.AccidentFactory
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.datasources.network.requests.AccidentListRequest
import motocitizen.datasources.network.requests.AccidentRequest
import motocitizen.datasources.network.requests.DetailsRequest
import motocitizen.datasources.network.requests.HasNewRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object Content {
    var lastUpdate: Long = 0
    var inPlace: Int = 0
    val accidents: TreeMap<Int, Accident> = TreeMap()
    val volunteers: TreeMap<Int, Volunteer> = TreeMap()

    //todo smell
    fun requestUpdate(callback: (JSONObject) -> Unit) {
        if (lastUpdate == 0L) {
            requestList(callback)
        } else {
            HasNewRequest(lastUpdate,
                          { response ->
                              if (response.getJSONArray("r")[0] == "y") requestList(callback) else callback(response)
                          })
        }
        lastUpdate = Date().time / 1000
    }

    private fun requestList(callback: (JSONObject) -> Unit) {
        AccidentListRequest({ response ->
                                parseGetListResponse(response)
                                callback(response)
                            })
    }

    fun requestAccident(id: Int, callback: (JSONObject) -> Unit) {
        AccidentRequest(id, { response ->
            parseGetListResponse(response)
            callback(response)
        })
    }

    fun requestDetailsForAccident(accident: Accident, callback: (JSONObject) -> Unit) {
        DetailsRequest(accident.id, { result ->
            attachDetailsToAccident(accident, result)
            callback(result)
        })
    }

    private fun attachDetailsToAccident(accident: Accident, result: JSONObject) {
        try {
            addVolunteers(result.getJSONObject("r").getJSONObject("u"))
            attachVolunteersToAccident(accident, result.getJSONObject("r").getJSONArray("v"))
            attachMessagesToAccident(accident, result.getJSONObject("r").getJSONArray("m"))
            attachHistoryToAccident(accident, result.getJSONObject("r").getJSONArray("h"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun attachVolunteersToAccident(accident: Accident, result: JSONArray) {
        accident.volunteers.clear()
        (0 until result.length()).mapTo(accident.volunteers) { VolunteerAction(result.getJSONObject(it)) }
    }

    private fun attachMessagesToAccident(accident: Accident, result: JSONArray) {
        accident.messages.clear()
        (0 until result.length())
                .map { Message(result.getJSONObject(it)) }
                .forEach { accident.messages.add(it) }
        accident.messages.sortBy { it.id }
    }

    private fun attachHistoryToAccident(accident: Accident, result: JSONArray) {
        accident.history.clear()
        (0 until result.length()).forEach { i -> accident.history.add(History(result.getJSONObject(i))) }
    }

    private fun parseGetListResponse(result: JSONObject) {
        try {
            addVolunteers(result.getJSONObject("r").getJSONObject("u"))
            addAccidents(result.getJSONObject("r").getJSONArray("l"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addVolunteers(volunteersList: JSONObject) {
        volunteersList.keys()
                .forEach { volunteers.put(it.toInt(), Volunteer(it.toInt(), volunteersList.getString(it))) }
    }

    private fun addAccidents(accidents: JSONArray) {
        (0 until accidents.length())
                .map { AccidentFactory.make(accidents.getJSONObject(it)) }
                .forEach { this.accidents.put(it.id, it) }
    }

    fun getListReversed() = accidents.values.sortedByDescending { it.id }
}
