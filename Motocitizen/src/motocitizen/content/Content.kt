package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.accident.AccidentFactory
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.network.CoreRequest
import motocitizen.network.requests.AccidentListRequest
import motocitizen.network.requests.AccidentRequest
import motocitizen.network.requests.DetailsRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object Content {
    var inPlace: Int = 0
    val accidents: TreeMap<Int, Accident> = TreeMap()

    val volunteers: TreeMap<Int, Volunteer> = TreeMap()

    fun requestUpdate(callback: CoreRequest.RequestResultCallback? = null) {
        AccidentListRequest(object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                parseJSON(response)
                callback?.call(response)
            }
        })
    }

    fun requestAccident(id: Int, callback: CoreRequest.RequestResultCallback?) {
        AccidentRequest(id, object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                parseJSON(response)
                callback?.call(response)
            }
        })
    }

    fun requestDetailsForAccident(accident:Accident, callback: CoreRequest.RequestResultCallback){
        DetailsRequest(accident.id, object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                try {
                    addVolunteers(response.getJSONObject("r").getJSONObject("u"))
                    val volunteersJSON = response.getJSONObject("r").getJSONArray("v")
                    val messagesJSON = response.getJSONObject("r").getJSONArray("m")
                    val historyJSON = response.getJSONObject("r").getJSONArray("h")
                    (0 until volunteersJSON.length()).mapTo(accident.volunteers) { VolunteerAction(volunteersJSON.getJSONObject(it)) }
                    (0 until messagesJSON.length())
                            .map { Message(messagesJSON.getJSONObject(it)) }
                            .forEach { accident.messages.add(it) }
                    accident.messages.sortBy { it.id }
                    for (i in 0 until historyJSON.length()) accident.history.add(History(historyJSON.getJSONObject(i)))
                } catch (e: JSONException) {
                    e.printStackTrace()
                } finally {
                    callback.call(response)
                }
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

    fun getListReversed() = accidents.values.sortedByDescending { it.id }
}
