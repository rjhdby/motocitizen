package motocitizen.content.accident

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.history.History
import motocitizen.content.message.Message
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
    private val MS_IN_HOUR = 3_600_000
    abstract val status: AccidentStatus
    val messages = ArrayList<Message>()
    val volunteers = ArrayList<VolunteerAction>()
    val history = ArrayList<History>()
    var messagesCount = 0
    var description: String = ""
        set(value) {
            field = value.trim()
        }

    fun distanceString(): String = if (metersFromUser() > 1000) {
        kiloMetersFromUser().toString() + "км"
    } else {
        metersFromUser().toString() + "м"
    }

    private fun metersFromUser(): Int = Math.round(location().distanceTo(MyLocationManager.getLocation()))
    private fun kiloMetersFromUser(): Float = (metersFromUser() / 10).toFloat() / 100

    fun location(): Location {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = coordinates.latitude
        location.longitude = coordinates.longitude
        return location
    }

    fun messagesCount(): Int = if (messages.isEmpty()) messagesCount else messages.count()

    fun isInvisible(): Boolean {
        val hidden = status == HIDDEN && !User.isModerator
        val distanceFilter = kiloMetersFromUser() > Preferences.visibleDistance
        val typeFilter = Preferences.isHidden(type)
        val timeFilter = time.time + Preferences.hoursAgo.toLong() * MS_IN_HOUR < Date().time
        return hidden || distanceFilter || typeFilter || timeFilter
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
