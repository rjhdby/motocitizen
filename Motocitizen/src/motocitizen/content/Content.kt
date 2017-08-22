package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.accident.AccidentFactory
import motocitizen.content.volunteer.Volunteer
import motocitizen.network.CoreRequest
import motocitizen.network.requests.AccidentListRequest
import motocitizen.network.requests.AccidentRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object Content {
    var inPlace: Int = 0
    val accidents: TreeMap<Int, Accident> = TreeMap()

    val volunteers: TreeMap<Int, Volunteer> = TreeMap()

    fun requestUpdate(callback: CoreRequest.RequestResultCallback?) {
        AccidentListRequest(object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                parseJSON(response)
                callback?.call(response)
            }
        })
    }

    fun requestUpdate() {
        requestUpdate(null)
    }

    fun requestAccident(id: Int, callback: CoreRequest.RequestResultCallback?) {
        AccidentRequest(id, object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                parseJSON(response)
                callback?.call(response)
            }
        })
    }

    private fun parseJSON(result: JSONObject) {
        try {
            addVolunteers(result.getJSONObject("r").getJSONObject("u"))
            val list = result.getJSONObject("r").getJSONArray("l")
            (0 until list.length())
                    .map { AccidentFactory.make(list.getJSONObject(it)) }
                    .forEach { accidents.put(it.id, it) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun addVolunteers(volunteersList: JSONObject) {
        volunteersList.keys()
                .forEach { volunteers.put(it.toInt(), Volunteer(it.toInt(), volunteersList.getString(it))) }
    }

    fun setLeave(currentInplace: Int) {
        //TODO SetLeave
    }

    fun getListReversed() = accidents.values.sortedByDescending { it.id }
}
