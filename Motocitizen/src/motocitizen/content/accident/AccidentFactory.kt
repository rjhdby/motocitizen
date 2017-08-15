package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.message.MessageFactory
import motocitizen.content.volunteer.Volunteer
import motocitizen.database.StoreMessages
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.user.Owner
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object AccidentFactory {
    fun make(json: JSONObject): Accident {
        return AccidentBuilder()
                .setId(json.getInt("id"))
                .setStatus(AccidentStatus.parse(json.getString("status")))
                .setType(Type.parse(json.getString("type")))
                .setMedicine(Medicine.parse(json.getString("med")))
                .setTime(Date(json.getLong("uxtime") * 1000))
                .setAddress(json.getString("address"))
                .setCoordinates(LatLng(json.getDouble("lat"), json.getDouble("lon")))
                .setOwner(Owner(json.getInt("owner_id"), json.getString("owner")))
                .attachMessages(parseMessages(json.getJSONArray("m"), StoreMessages.getLast(json.getInt("id"))))
                .attachVolunteers(parseVolunteers(json.getJSONArray("v")))
                .attachHistory(parseHistory(json.getJSONArray("h")))
                .setDescription(json.getString("descr"))
                .build()
    }

    fun refactor(accident: Accident, status: AccidentStatus): Accident {
        return AccidentBuilder()
                .from(accident)
                .setStatus(status)
                .build()
    }

    private fun parseMessages(json: JSONArray, last: Int): TreeMap<Int, Message> {
        val messages = TreeMap<Int, Message>()

        (0..json.length() - 1)
                .map {
                    try {
                        MessageFactory.make(json.getJSONObject(it))
                    } catch (e: Exception) {
                        null
                    }
                }
                .filterNotNull()
                .filter { !messages.containsKey(it.id) }
                .forEach {
                    if (it.id <= last) it.read = true
                    messages.put(it.id, it)
                }

        return messages
    }

    private fun parseVolunteers(json: JSONArray): TreeMap<Int, Volunteer> {
        val volunteers = TreeMap<Int, Volunteer>()
        (0..json.length() - 1)
                .map {
                    try {
                        Volunteer(json.getJSONObject(it))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                .filterNotNull()
                .forEach { volunteers.put(it.id, it) }
        return volunteers
    }

    @Throws(JSONException::class)
    private fun parseHistory(json: JSONArray): TreeMap<Int, History> {
        val history = TreeMap<Int, History>()
        (0..json.length() - 1)
                .map {
                    try {
                        History(json.getJSONObject(it))
                    } catch (e: Exception) {
                        null
                    }
                }
                .filterNotNull()
                .forEach { history.put(it.id, it) }
        return history
    }
}