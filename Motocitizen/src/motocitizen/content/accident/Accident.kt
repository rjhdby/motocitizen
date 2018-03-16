package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.datasources.preferences.Preferences
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.ACTIVE
import motocitizen.dictionary.AccidentStatus.HIDDEN
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import motocitizen.user.User
import motocitizen.utils.Id
import motocitizen.utils.MS_IN_HOUR
import motocitizen.utils.distanceString
import motocitizen.utils.metersFromUser
import java.util.*
import kotlin.collections.ArrayList

//todo simplify constructor
class Accident(
        val id: Id,
        var type: Type,
        var medicine: Medicine,
        val time: Date,
        var location: AccidentLocation,
        val owner: Id,
        var status: AccidentStatus) {

    val volunteers = ArrayList<VolunteerAction>()
    val history = ArrayList<History>()
    var messagesCount = 0
    var description: String = ""
        set(value) {
            field = value.trim()
        }

    var coordinates: LatLng = location.coordinates
        get() = location.coordinates
        private set

    var address: String = location.address
        get() = location.address
        private set

    fun isVisible() = when {
        User.notIsModerator() && status == HIDDEN                            -> false
        coordinates.metersFromUser() > Preferences.visibleDistance * 1000    -> false
        !Preferences.isEnabled(type)                                         -> false
        time.time + Preferences.hoursAgo.toLong() * MS_IN_HOUR < Date().time -> false
        else                                                                 -> true
    }

    fun isAccident(): Boolean = type in arrayOf(Type.MOTO_AUTO, Type.MOTO_MOTO, Type.MOTO_MAN, Type.SOLO)

    fun isOwner(): Boolean = owner == User.id

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN || !isAccident()) "" else ", " + medicine.text
        return String.format("%s%s(%s)%n%s%n%s", type.text, damage, distanceString(), address, description)
    }

    fun isActive() = status == ACTIVE
    fun isEnded() = !isActive()
    fun isHidden() = status == HIDDEN
}