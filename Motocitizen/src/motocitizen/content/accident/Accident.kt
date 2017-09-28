package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.history.History
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.datasources.preferences.Preferences
import motocitizen.datasources.preferences.Preferences.Stored.HOURS_AGO
import motocitizen.datasources.preferences.Preferences.Stored.VISIBLE_DISTANCE
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.HIDDEN
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import motocitizen.user.User
import motocitizen.utils.MS_IN_HOUR
import motocitizen.utils.distanceString
import motocitizen.utils.metersFromUser
import java.util.*
import kotlin.collections.ArrayList

//todo simplify constructor
abstract class Accident(val id: Int, var type: Type, var medicine: Medicine, val time: Date, var location: AccidentLocation, val owner: Int) {
    abstract val status: AccidentStatus
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

    fun ownerName() = Content.volunteerName(owner)

    fun distanceString(): String = distanceString(coordinates)

    fun isVisible(): Boolean {
        val visible = User.isModerator || status != HIDDEN
        val distanceFilter = metersFromUser(coordinates) < VISIBLE_DISTANCE.int() * 1000
        val settingsFilter = Preferences.isEnabled(type)
        val timeFilter = time.time + HOURS_AGO.int().toLong() * MS_IN_HOUR > Date().time
        return visible && distanceFilter && settingsFilter && timeFilter
    }

    fun isAccident(): Boolean = type == Type.MOTO_AUTO || type == Type.MOTO_MOTO || type == Type.MOTO_MAN || type == Type.SOLO

    fun isOwner(): Boolean = owner == User.id

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN || !isAccident()) "" else ", " + medicine.text
        return String.format("%s%s(%s)%n%s%n%s", type.text, damage, distanceString(), address, description)
    }
}
