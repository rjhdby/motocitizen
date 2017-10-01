package motocitizen.content.accident

import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import java.util.*

class HiddenAccident(id: Int, type: Type, damage: Medicine, time: Date, location: AccidentLocation, owner: Int) : Accident(id, type, damage, time, location, owner) {
    override var status = AccidentStatus.HIDDEN
}