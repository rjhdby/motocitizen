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
import motocitizen.geolocation.MyLocationManager
import motocitizen.user.Owner
import motocitizen.user.User
import motocitizen.utils.SortedHashMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AccidentFactory {
    companion object {
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

        private fun parseMessages(json: JSONArray, last: Int): SortedHashMap<Message> {
            val messages = SortedHashMap<Message>()

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

        private fun parseVolunteers(json: JSONArray): SortedHashMap<Volunteer> {
            val volunteers = SortedHashMap<Volunteer>()
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
        private fun parseHistory(json: JSONArray): SortedHashMap<History> {
            val history = SortedHashMap<History>()
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

    class AccidentBuilder {
        var id = 0
            private set
        var status = AccidentStatus.ACTIVE
            private set
        var type = Type.OTHER
            private set
        var medicine = Medicine.NO
            private set
        var time = Date()
            private set
        var address = ""
            private set
        var coordinates = LatLng(MyLocationManager.getLocation().latitude, MyLocationManager.getLocation().longitude)
            private set
        var owner = Owner(User.dirtyRead().id, User.dirtyRead().name)
            private set
        var description = ""
            private set
        var messages = SortedHashMap<Message>()
            private set
        var volunteers = SortedHashMap<Volunteer>()
            private set
        var history = SortedHashMap<History>()
            private set

        fun setId(id: Int): AccidentBuilder {
            this.id = id
            return this
        }

        fun setStatus(status: AccidentStatus): AccidentBuilder {
            this.status = status
            return this
        }

        fun setType(type: Type): AccidentBuilder {
            this.type = type
            return this
        }

        fun setMedicine(medicine: Medicine): AccidentBuilder {
            this.medicine = medicine
            return this
        }

        fun setTime(time: Date): AccidentBuilder {
            this.time = time
            return this
        }

        fun setAddress(address: String): AccidentBuilder {
            this.address = address
            return this
        }

        fun setCoordinates(coordinates: LatLng): AccidentBuilder {
            this.coordinates = coordinates
            return this
        }

        fun setOwner(owner: Owner): AccidentBuilder {
            this.owner = owner
            return this
        }

        fun setDescription(description: String): AccidentBuilder {
            this.description = description
            return this
        }

        fun attachMessages(messages: SortedHashMap<Message>): AccidentBuilder {
            this.messages = messages
            return this
        }

        fun attachVolunteers(volunteers: SortedHashMap<Volunteer>): AccidentBuilder {
            this.volunteers = volunteers
            return this
        }

        fun attachHistory(history: SortedHashMap<History>): AccidentBuilder {
            this.history = history
            return this
        }

        fun from(accident: Accident): AccidentBuilder {
            id = accident.id
            type = accident.type
            medicine = accident.medicine
            time = accident.time
            address = accident.address
            coordinates = accident.coordinates
            owner = accident.owner
            description = accident.description
            messages = accident.messages
            volunteers = accident.volunteers
            history = accident.history
            return this
        }

        fun build(): Accident {
            val accident = when {
                status == AccidentStatus.ACTIVE && owner.isUser -> OwnedActiveAccident(id, type, medicine, time, address, coordinates, owner)
                status == AccidentStatus.ENDED && owner.isUser  -> OwnedEndedAccident(id, type, medicine, time, address, coordinates, owner)
                status == AccidentStatus.HIDDEN && owner.isUser -> OwnedHiddenAccident(id, type, medicine, time, address, coordinates, owner)
                status == AccidentStatus.ACTIVE                 -> ActiveAccident(id, type, medicine, time, address, coordinates, owner)
                status == AccidentStatus.ENDED                  -> EndedAccident(id, type, medicine, time, address, coordinates, owner)
                status == AccidentStatus.HIDDEN                 -> HiddenAccident(id, type, medicine, time, address, coordinates, owner)
                else                                            -> throw Exception("Wrong data from server")
            }
            accident.description = description
            accident.messages = messages
            accident.volunteers = volunteers
            accident.history = history
            return accident
        }
    }
}
