package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.user.User
import java.util.*

class OwnedHiddenAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng) : Accident(id, type, damage, time, address, coordinates, User.id) {
    override val status = AccidentStatus.HIDDEN
}