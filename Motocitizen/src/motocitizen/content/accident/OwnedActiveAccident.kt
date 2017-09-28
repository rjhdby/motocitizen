package motocitizen.content.accident

import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import motocitizen.user.User
import java.util.*

class OwnedActiveAccident(id: Int, type: Type, damage: Medicine, time: Date, location: AccidentLocation) : Accident(id, type, damage, time, location, User.id) {
    override val status = AccidentStatus.ACTIVE
}