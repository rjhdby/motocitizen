package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.user.User
import java.util.*
import kotlin.collections.ArrayList

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
    var coordinates = MyLocationManager.getLocation()
        private set
    var owner = User.id
        private set
    var description = ""
        private set
//    var messages = ArrayList<Message>()
//        private set
    var volunteers = ArrayList<VolunteerAction>()
        private set
    var history = ArrayList<History>()
        private set
    var messagesCount = 0
        private set

    fun id(id: Int): AccidentBuilder = apply { this.id = id }

    fun status(status: AccidentStatus): AccidentBuilder = apply { this.status = status }

    fun type(type: Type): AccidentBuilder = apply { this.type = type }

    fun medicine(medicine: Medicine): AccidentBuilder = apply { this.medicine = medicine }

    fun time(time: Date): AccidentBuilder = apply { this.time = time }

    fun address(address: String): AccidentBuilder = apply { this.address = address }

    fun coordinates(coordinates: LatLng): AccidentBuilder = apply { this.coordinates = coordinates }

    fun owner(owner: Int): AccidentBuilder = apply { this.owner = owner }

    fun description(description: String): AccidentBuilder = apply { this.description = description }

    fun messagesCount(count: Int): AccidentBuilder = apply { this.messagesCount = count }

    fun from(accident: Accident): AccidentBuilder {
        id = accident.id
        type = accident.type
        medicine = accident.medicine
        time = accident.time
        address = accident.address
        coordinates = accident.coordinates
        owner = accident.owner
        description = accident.description
//        messages = accident.messages
        volunteers = accident.volunteers
        history = accident.history
        messagesCount = accident.messagesCount
        return this
    }

    fun build(): Accident {
        val accident = when {
            status == AccidentStatus.ACTIVE && owner == User.id -> OwnedActiveAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.ENDED && owner == User.id  -> OwnedEndedAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.HIDDEN && owner == User.id -> OwnedHiddenAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.ACTIVE                     -> ActiveAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.ENDED                      -> EndedAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.HIDDEN                     -> HiddenAccident(id, type, medicine, time, address, coordinates, owner)
            else                                                -> throw Exception("Wrong data from server")
        }
        accident.description = description
//        accident.messages.addAll(messages)
        accident.volunteers.addAll(volunteers)
        accident.history.addAll(history)
        accident.messagesCount = messagesCount
        return accident
    }
}