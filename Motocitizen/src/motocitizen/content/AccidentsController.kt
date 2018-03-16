package motocitizen.content

import kotlinx.coroutines.experimental.runBlocking
import motocitizen.content.accident.Accident
import motocitizen.content.accident.AccidentFactory
import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.AccidentListRequest
import motocitizen.datasources.network.requests.AccidentRequest
import motocitizen.datasources.network.requests.DetailsRequest
import motocitizen.datasources.network.requests.HasNewRequest
import motocitizen.utils.asList
import motocitizen.utils.asyncMap
import motocitizen.utils.seconds
import org.json.JSONArray
import org.json.JSONException
import java.util.*

object AccidentsController {
    private var lastUpdate: Long = 0
    val accidents: TreeMap<Int, Accident> = TreeMap()

    fun resetLastUpdate() {
        lastUpdate = 0
    }

    fun update(callback: (ApiResponse) -> Unit) {
        if (lastUpdate == 0L) {
            requestList(callback)
        } else {
            HasNewRequest(lastUpdate) { if (hasNewCheck(it)) requestList(callback) else callback(it) }.call()
        }
    }

    private fun hasNewCheck(response: ApiResponse): Boolean = try {
        response.resultArray[0] == "y"
    } catch (e: Exception) {
        true
    }

    private fun requestList(callback: (ApiResponse) -> Unit) {
        AccidentListRequest { listRequestCallback(it, callback) }.call()
    }

    private inline fun listRequestCallback(response: ApiResponse, callback: (ApiResponse) -> Unit) {
        parseGetListResponse(response)
        lastUpdate = Date().seconds()
        callback(response)
    }

    fun requestDetailsForAccident(accident: Accident, callback: (ApiResponse) -> Unit) {
        DetailsRequest(accident.id) {
            attachDetailsToAccident(accident, it)
            callback(it)
        }.call()
    }

    private fun attachDetailsToAccident(accident: Accident, result: ApiResponse) = try {
        Content.addVolunteers(result.resultObject.getJSONObject("u"))
        attachVolunteersToAccident(accident, result.resultObject.getJSONArray("v"))
        Content.addMessages(result.resultObject.getJSONArray("m"))
        attachHistoryToAccident(accident, result.resultObject.getJSONArray("h"))
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    fun requestSingleAccident(id: Int, callback: (ApiResponse) -> Unit) {
        AccidentRequest(id, {
            parseGetListResponse(it)
            callback(it)
        }).call()
    }

    private fun parseGetListResponse(apiResponse: ApiResponse) = try {
        Content.addVolunteers(apiResponse.resultObject.getJSONObject("u"))
        addAccidents(apiResponse.resultObject.getJSONArray("l"))
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    private fun addAccidents(json: JSONArray) = runBlocking {
        (0 until json.length())
                .asyncMap { AccidentFactory.make(json.getJSONObject(it)) }
                .forEach { accidents[it.id] = it }
    }

    private fun attachVolunteersToAccident(accident: Accident, json: JSONArray) {
        accident.volunteers.clear()
        json.asList().mapTo(accident.volunteers) { VolunteerAction(it) }
    }

    private fun attachHistoryToAccident(accident: Accident, json: JSONArray) {
        accident.history.clear()
        json.asList().forEach { accident.history.add(History(it)) }
    }
}