package motocitizen.content.accident

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.ACTIVE
import motocitizen.dictionary.AccidentStatus.HIDDEN
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geolocation.MyLocationManager
import motocitizen.user.User
import motocitizen.utils.Preferences
import java.util.*
import kotlin.collections.ArrayList

abstract class Accident(val id: Int, var type: Type, var medicine: Medicine, val time: Date, var address: String, var coordinates: LatLng, val owner: Int) {
    abstract val status: AccidentStatus

    var description: String = ""
        set(value) {
            field = value.replace("^\\s+", "").replace("\\s+$", "")
        }

    var messages = TreeMap<Int, Message>()
    val volunteers = ArrayList<VolunteerAction>()
    var history = ArrayList<History>()

    val distanceString: String
        get() = if (distanceFromUser > 1000) {
            (Math.round(distanceFromUser / 10) / 100).toString() + "км"
        } else {
            Math.round(distanceFromUser).toString() + "м"
        }

    private val distanceFromUser: Double
        get() = location.distanceTo(MyLocationManager.getLocation()).toDouble()

    val location: Location
        get() {
            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = coordinates.latitude
            location.longitude = coordinates.longitude
            return location
        }

    val unreadMessagesCount: Int
        get() = messages.keys.count { !messages[it]!!.read }

    fun isInvisible(context: Context): Boolean {
        val hidden = status == HIDDEN && !User.dirtyRead().isModerator
        val distanceFilter = distanceFromUser > Preferences.getInstance(context).visibleDistance * 1000
        val typeFilter = Preferences.getInstance(context).isHidden(type)
        val timeFilter = time.time + Preferences.getInstance(context).hoursAgo.toLong() * 60 * 60 * 1000 < Date().time
        return hidden || distanceFilter || typeFilter || timeFilter
    }

    fun getVolunteer(id: Int): Volunteer? = Content.volunteers[id]

    fun isActive(): Boolean = status === ACTIVE

    fun isEnded(): Boolean = status !== ACTIVE

    fun isHidden(): Boolean = status === HIDDEN

    val isAccident: Boolean
        get() = type == Type.MOTO_AUTO || type == Type.MOTO_MOTO || type == Type.MOTO_MAN || type == Type.SOLO

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN) "" else ", " + medicine.text
        return String.format("%s%s(%s)%n%s%n%s", type.text, damage, distanceString, address, description)
    }
}
