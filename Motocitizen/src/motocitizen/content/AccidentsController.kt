package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.accident.AccidentFactory
import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.datasources.network.requests.AccidentListRequest
import motocitizen.datasources.network.requests.AccidentRequest
import motocitizen.datasources.network.requests.DetailsRequest
import motocitizen.datasources.network.requests.HasNewRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object AccidentsController {
    var lastUpdate: Long = 0
    val accidents: TreeMap<Int, Accident> = TreeMap()

    fun update(callback: (JSONObject) -> Unit) {
        if (lastUpdate == 0L) {
            requestList(callback)
        } else {
            HasNewRequest(lastUpdate,
                          { response ->
                              if (response.getJSONArray("r")[0] == "y") requestList(callback) else callback(response)
                          })
        }
    }

    private fun requestList(callback: (JSONObject) -> Unit) {
        AccidentListRequest({ response ->
                                parseGetListResponse(response)
                                lastUpdate = Date().time / 1000
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
            Content.addVolunteers(result.getJSONObject("r").getJSONObject("u"))
            attachVolunteersToAccident(accident, result.getJSONObject("r").getJSONArray("v"))
            Content.addMessages(result.getJSONObject("r").getJSONArray("m"))
            attachHistoryToAccident(accident, result.getJSONObject("r").getJSONArray("h"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun parseGetListResponse(result: JSONObject) {
        try {
            Content.addVolunteers(result.getJSONObject("r").getJSONObject("u"))
            addAccidents(result.getJSONObject("r").getJSONArray("l"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun requestSingleAccident(id: Int, callback: (JSONObject) -> Unit) {
        AccidentRequest(id, { response ->
            parseGetListResponse(response)
            callback(response)
        })
    }

    private fun addAccidents(json: JSONArray) {
        (0 until json.length())
                .map { AccidentFactory.make(json.getJSONObject(it)) }
                .forEach { accidents.put(it.id, it) }
    }

    private fun attachVolunteersToAccident(accident: Accident, json: JSONArray) {
        accident.volunteers.clear()
        (0 until json.length()).mapTo(accident.volunteers) { VolunteerAction(json.getJSONObject(it)) }
    }

    private fun attachHistoryToAccident(accident: Accident, json: JSONArray) {
        accident.history.clear()
        (0 until json.length()).forEach { i -> accident.history.add(History(json.getJSONObject(i))) }
    }
}