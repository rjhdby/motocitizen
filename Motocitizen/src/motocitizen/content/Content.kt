package motocitizen.content

import motocitizen.content.accident.Accident
import motocitizen.content.accident.AccidentFactory
import motocitizen.content.volunteer.Volunteer
import motocitizen.network.CoreRequest
import motocitizen.network.requests.AccidentListRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object Content {
    var inPlace: Int = 0
    val accidents: TreeMap<Int, Accident> = TreeMap()
    val volunteers: TreeMap<Int, Volunteer> = TreeMap()

    fun requestUpdate(listener: CoreRequest.RequestResultCallback) {
        AccidentListRequest(listener)
    }

    fun requestUpdate() {
        AccidentListRequest(object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                if (!response.has("error")) parseJSON(response)
            }
        })
    }

    fun parseJSON(result: JSONObject) { //todo make private
        try {
            val volunteersList = result.getJSONObject("r").getJSONObject("u")
            volunteersList.keys()
                    .forEach { volunteers.put(it.toInt(), Volunteer(it.toInt(), volunteersList.getString(it))) }

            val list = result.getJSONObject("r").getJSONArray("l")
            (0 until list.length())
                    .map { AccidentFactory.makeNew(list.getJSONObject(it)) }
                    .forEach { accidents.put(it.id, it) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun parseJSON(result: JSONObject, id: Int) = parseJSON(result)

    fun setLeave(currentInplace: Int) {
        //TODO SetLeave
    }
}
