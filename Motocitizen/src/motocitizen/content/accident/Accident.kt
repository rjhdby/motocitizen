package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.ACTIVE
import motocitizen.dictionary.AccidentStatus.HIDDEN
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.user.User
import motocitizen.utils.Preferences
import motocitizen.utils.metersFromUser
import java.util.*
import kotlin.collections.ArrayList

abstract class Accident(val id: Int, var type: Type, var medicine: Medicine, val time: Date, var address: String, var coordinates: LatLng, val owner: Int) {
    private val MS_IN_HOUR = 3_600_000
    abstract val status: AccidentStatus
    val volunteers = ArrayList<VolunteerAction>()
    val history = ArrayList<History>()
    var messagesCount = 0
    var description: String = ""
        set(value) {
            field = value.trim()
        }

    fun ownerName() = Content.volunteer(owner).name

    fun distanceString(): String = motocitizen.utils.distanceString(coordinates)

//    fun location(): Location {
//        val location = Location(LocationManager.GPS_PROVIDER)
//        location.latitude = coordinates.latitude
//        location.longitude = coordinates.longitude
//        return location
//    }

    fun messagesCount(): Int = messagesCount

    fun isVisible(): Boolean {
        val visible = User.isModerator || status != HIDDEN
        val distanceFilter = metersFromUser(coordinates) < Preferences.visibleDistance * 1000
        val settingsFilter = Preferences.isEnabled(type)
        val timeFilter = time.time + Preferences.hoursAgo.toLong() * MS_IN_HOUR > Date().time
        return visible && distanceFilter && settingsFilter && timeFilter
    }

    fun isActive(): Boolean = status === ACTIVE

    fun isEnded(): Boolean = status !== ACTIVE

    fun isHidden(): Boolean = status === HIDDEN

    fun isAccident(): Boolean = type == Type.MOTO_AUTO || type == Type.MOTO_MOTO || type == Type.MOTO_MAN || type == Type.SOLO

    fun isOwner(): Boolean = owner == User.id

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN || !isAccident()) "" else ", " + medicine.text
        return String.format("%s%s(%s)%n%s%n%s", type.text, damage, distanceString(), address, description)
    }
}
