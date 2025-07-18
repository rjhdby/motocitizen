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
import java.util.Date

//todo simplify constructor
class Accident(
    val id: Id,
    var type: Type,
    var medicine: Medicine,
    val time: Date,
    var location: AccidentLocation,
    val owner: Id,
    var status: AccidentStatus,
    var description: String = "",
    var messagesCount: Int = 0,
) {

    val volunteers = ArrayList<VolunteerAction>()
    val history = ArrayList<History>()

    var coordinates: LatLng = location.coordinates
        get() = location.coordinates
        private set

    var address: String = location.address
        get() = location.address
        private set

    fun isVisible() = when {
        User.notIsModerator() && status == HIDDEN -> false
        coordinates.metersFromUser() > Preferences.visibleDistance * 1000 -> false
        !Preferences.isEnabled(type) -> false
        time.time + Preferences.hoursAgo.toLong() * MS_IN_HOUR < Date().time -> false
        else -> true
    }

    fun isAccident(): Boolean = type in arrayOf(Type.MOTO_AUTO, Type.MOTO_MOTO, Type.MOTO_MAN, Type.SOLO)

    fun isOwner(): Boolean = owner == User.id

    fun header(): String {
        val damage = if (medicine == Medicine.UNKNOWN || !isAccident()) "" else ", " + medicine.text
        return String.format("%s%s(%s)", type.text, damage, distanceString())
    }

    fun title(): String {
        val damage = if (medicine == Medicine.UNKNOWN || !isAccident()) "" else ", " + medicine.text
        val escapedDescription = Regex("""https://yandex\.ru\S*""").replace(description) { matchResult ->
            val url = matchResult.value
            """<a href="$url">Яндекс карты</a>"""
        }
        return String.format("%s%s(%s)%n%s%n%s", type.text, damage, distanceString(), address, escapedDescription)
    }

    fun isActive() = status == ACTIVE
    fun isFinished() = !isActive()
    fun isHidden() = status == HIDDEN
    fun body(): String {
        return yandexUrlRegex.replace(description, "").trim()
    }

    fun extractYandexUrl(): String? {
        val url = yandexUrlRegex.find(description)?.value ?: return null
        return """<a href="$url">Яндекс карты</a>"""
    }

    companion object {
        private val yandexUrlRegex = Regex("""https://yandex\.ru\S*""")
    }
}