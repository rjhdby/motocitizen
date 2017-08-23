package motocitizen.content.accident

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.ACTIVE
import motocitizen.dictionary.AccidentStatus.HIDDEN
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geolocation.MyLocationManager
import motocitizen.network.CoreRequest
import motocitizen.network.requests.DetailsRequest
import motocitizen.user.User
import motocitizen.utils.Preferences
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

abstract class Accident(val id: Int, var type: Type, var medicine: Medicine, val time: Date, var address: String, var coordinates: LatLng, val owner: Int) {
    abstract val status: AccidentStatus

    var description: String = ""
        set(value) {
            field = value.trim()
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

    fun isActive(): Boolean = status === ACTIVE

    fun isEnded(): Boolean = status !== ACTIVE

    fun isHidden(): Boolean = status === HIDDEN

    fun isAccident(): Boolean = type == Type.MOTO_AUTO || type == Type.MOTO_MOTO || type == Type.MOTO_MAN || type == Type.SOLO

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN || !isAccident()) "" else ", " + medicine.text
        return String.format("%s%s(%s)%n%s%n%s", type.text, damage, distanceString, address, description)
    }

    fun requestDetails(callback: CoreRequest.RequestResultCallback) {
        DetailsRequest(id, object : CoreRequest.RequestResultCallback {
            override fun call(response: JSONObject) {
                try {
                    Content.addVolunteers(response.getJSONObject("r").getJSONObject("u"))
                    val volunteersJSON = response.getJSONObject("r").getJSONArray("v")
                    val messagesJSON = response.getJSONObject("r").getJSONArray("m")
                    val historyJSON = response.getJSONObject("r").getJSONArray("h")
                    (0 until volunteersJSON.length()).mapTo(volunteers) { VolunteerAction(volunteersJSON.getJSONObject(it)) }
                    (0 until messagesJSON.length())
                            .map { Message(messagesJSON.getJSONObject(it)) }
                            .forEach { messages.put(it.id, it) }
                    for (i in 0 until historyJSON.length()) history.add(History(historyJSON.getJSONObject(i)))
                } catch (e: JSONException) {
                    e.printStackTrace()
                } finally {
                    callback.call(response)
                }
            }
        })
    }
}
