package motocitizen.content

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.accident.*
import motocitizen.content.message.Message
import motocitizen.database.StoreMessages
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.user.Owner
import motocitizen.utils.SortedHashMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AccidentFactory {
    companion object {
        var lastRead: Int = 0
        fun make(json: JSONObject): Accident {
            val id = json.getInt("id")
            val status = json.getString("status")
            val type = Type.parse(json.getString("type"))
            val medicine = Medicine.parse(json.getString("med"))
            val time = Date(json.getLong("uxtime") * 1000)
            val address = json.getString("address")
            val lat = json.getDouble("lat")
            val lon = json.getDouble("lon")
            lastRead = StoreMessages.getLast(id)
            val owner = Owner(json.getInt("owner_id"), json.getString("owner"))

            val accident = when {
                status == "acc_status_act" && owner.isUser  -> OwnedActiveAccident(id, type, medicine, time, address, LatLng(lat, lon), owner)
                status == "acc_status_end" && owner.isUser  -> OwnedEndedAccident(id, type, medicine, time, address, LatLng(lat, lon), owner)
                status == "acc_status_hide" && owner.isUser -> OwnedHiddenAccident(id, type, medicine, time, address, LatLng(lat, lon), owner)
                status == "acc_status_act"                  -> ActiveAccident(id, type, medicine, time, address, LatLng(lat, lon), owner)
                status == "acc_status_end"                  -> EndedAccident(id, type, medicine, time, address, LatLng(lat, lon), owner)
                status == "acc_status_hide"                 -> HiddenAccident(id, type, medicine, time, address, LatLng(lat, lon), owner)
                else                                        -> throw Exception("Wrong data from server")
            }
            accident.description = json.getString("descr")

            accident.messages = parseMessages(json.getJSONArray("m"))
            accident.volunteers = parseVolunteers(json.getJSONArray("v"))
            accident.history = parseHistory(json.getJSONArray("h"))
            return accident
        }

        fun refactor(accident: Accident, status: AccidentStatus): Accident {
            val newAccident = when {
                status == AccidentStatus.ACTIVE && accident.owner.isUser -> OwnedActiveAccident(accident.id, accident.type, accident.medicine, accident.time, accident.address, accident.coordinates, accident.owner)
                status == AccidentStatus.ENDED && accident.owner.isUser  -> OwnedEndedAccident(accident.id, accident.type, accident.medicine, accident.time, accident.address, accident.coordinates, accident.owner)
                status == AccidentStatus.HIDDEN && accident.owner.isUser -> OwnedHiddenAccident(accident.id, accident.type, accident.medicine, accident.time, accident.address, accident.coordinates, accident.owner)
                status == AccidentStatus.ACTIVE                          -> ActiveAccident(accident.id, accident.type, accident.medicine, accident.time, accident.address, accident.coordinates, accident.owner)
                status == AccidentStatus.ENDED                           -> EndedAccident(accident.id, accident.type, accident.medicine, accident.time, accident.address, accident.coordinates, accident.owner)
                status == AccidentStatus.HIDDEN                          -> HiddenAccident(accident.id, accident.type, accident.medicine, accident.time, accident.address, accident.coordinates, accident.owner)
                else                                                     -> throw Exception("Wrong data from server")
            }
            newAccident.description = accident.description

            newAccident.messages = accident.messages
            newAccident.volunteers = accident.volunteers
            newAccident.history = accident.history
            return newAccident
        }


        @Throws(JSONException::class)
        private fun parseMessages(json: JSONArray): SortedHashMap<Message> {
            val messages = SortedHashMap<Message>()
            for (i in 0..json.length() - 1) {
                try {
                    val message = MessageFactory.make(json.getJSONObject(i))
                    if (message.id <= lastRead) message.read = true
                    if (messages.containsKey(message.id)) continue
                    messages.put(message.id, message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return messages
        }

        @Throws(JSONException::class)
        private fun parseVolunteers(json: JSONArray): SortedHashMap<Volunteer> {
            val volunteers = SortedHashMap<Volunteer>()
            (0..json.length() - 1)
                    .map { Volunteer(json.getJSONObject(it)) }
                    .filter { it.isNoError }
                    .forEach { volunteers.put(it.id, it) }
            return volunteers
        }

        @Throws(JSONException::class)
        private fun parseHistory(json: JSONArray): SortedHashMap<History> {
            val history = SortedHashMap<History>()
            (0..json.length() - 1)
                    .map { History(json.getJSONObject(it)) }
                    .filter { it.isNoError }
                    .forEach { history.put(it.id, it) }
            return history
        }
    }
}
