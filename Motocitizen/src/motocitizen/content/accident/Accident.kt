package motocitizen.content.accident

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.HIDDEN
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geolocation.MyLocationManager
import motocitizen.rows.accidentList.Row
import motocitizen.user.Owner
import motocitizen.user.User
import motocitizen.utils.Preferences
import motocitizen.utils.SortedHashMap
import java.util.*

abstract class Accident(val id: Int, var type: Type, var medicine: Medicine, val time: Date, var address: String, var coordinates: LatLng, val owner: Owner) {
    abstract val status: AccidentStatus

    var description: String = ""
        set(value) {
            field = value.replace("^\\s+", "").replace("\\s+$", "")
        }

    var messages = SortedHashMap<Message>()
    var volunteers = SortedHashMap<Volunteer>()
    var history = SortedHashMap<History>()

    val distanceString: String
        get() {
            val distance = distanceFromUser
            if (distance > 1000) {
                return (Math.round(distance / 10) / 100).toString() + "км"
            } else {
                return Math.round(distance).toString() + "м"
            }
        }

    private val distanceFromUser: Double
        get() {
            val userLocation = MyLocationManager.getLocation()
            return location.distanceTo(userLocation).toDouble()
        }

    val location: Location
        get() {
            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = coordinates.latitude
            location.longitude = coordinates.longitude
            return location
        }

    val unreadMessagesCount: Int
        get() {
            val counter = messages.keys.count { !messages[it]!!.read }
            return counter
        }

    fun isInvisible(context: Context): Boolean {
        val hidden = status == HIDDEN && !User.dirtyRead().isModerator
        val distanceFilter = distanceFromUser > Preferences.getInstance(context).visibleDistance * 1000
        val typeFilter = Preferences.getInstance(context).isHidden(type)
        val timeFilter = time.time + Preferences.getInstance(context).hoursAgo.toLong() * 60 * 60 * 1000 < Date().time
        return hidden || distanceFilter || typeFilter || timeFilter
    }

    fun getVolunteer(id: Int): Volunteer? {
        return volunteers[id]
    }

    open fun isActive(): Boolean {
        return false
    }

    open fun isEnded(): Boolean {
        return false
    }

    open fun isHidden(): Boolean {
        return false
    }

    fun setLatLng(latLng: LatLng) {
        coordinates = latLng
    }

    val isAccident: Boolean
        get() = type == Type.MOTO_AUTO || type == Type.MOTO_MOTO || type == Type.MOTO_MAN || type == Type.SOLO

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN) "" else ", " + medicine.text
        return String.format("%s%s(%s)%n%s%n%s", type, damage, distanceString, address, description)
    }

    abstract fun makeListRow(context: Context): Row
}
