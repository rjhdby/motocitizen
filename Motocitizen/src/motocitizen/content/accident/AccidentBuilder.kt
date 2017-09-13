package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geolocation.MyLocationManager
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
    var coordinates = LatLng(MyLocationManager.getLocation().latitude, MyLocationManager.getLocation().longitude)
        private set
    var owner = User.id
        private set
    var description = ""
        private set
    var messages = ArrayList<Message>()
        private set
    var volunteers = ArrayList<VolunteerAction>()
        private set
    var history = ArrayList<History>()
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

    fun setOwner(owner: Int): AccidentBuilder {
        this.owner = owner
        return this
    }

    fun setDescription(description: String): AccidentBuilder {
        this.description = description
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
            status == AccidentStatus.ACTIVE && owner == User.id -> OwnedActiveAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.ENDED && owner == User.id  -> OwnedEndedAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.HIDDEN && owner == User.id -> OwnedHiddenAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.ACTIVE                     -> ActiveAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.ENDED                      -> EndedAccident(id, type, medicine, time, address, coordinates, owner)
            status == AccidentStatus.HIDDEN                     -> HiddenAccident(id, type, medicine, time, address, coordinates, owner)
            else                                                -> throw Exception("Wrong data from server")
        }
        accident.description = description
        accident.messages = messages
//        accident.volunteers = volunteers //todo
//        accident.history = history
        return accident
    }
}