package motocitizen.content.accident

import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.user.User
import java.util.*
import kotlin.collections.ArrayList

class AccidentBuilder {
    var id = 0
    var status = AccidentStatus.ACTIVE
    var type = Type.OTHER
    var medicine = Medicine.NO
    var time = Date()
    var location = AccidentLocation(coordinates = MyLocationManager.getLocation())
    var owner = User.id
    var description = ""
    var volunteers = ArrayList<VolunteerAction>()
    var history = ArrayList<History>()
    var messagesCount = 0

    fun from(accident: Accident) = apply {
        id = accident.id
        type = accident.type
        medicine = accident.medicine
        time = accident.time
        location = accident.location
        owner = accident.owner
        description = accident.description
        volunteers = accident.volunteers
        history = accident.history
        messagesCount = accident.messagesCount
    }

    fun build(): Accident {
        val accident = Accident(id, type, medicine, time, location, owner, status)

        accident.description = description
        accident.volunteers.addAll(volunteers)
        accident.history.addAll(history)
        accident.messagesCount = messagesCount
        return accident
    }
}