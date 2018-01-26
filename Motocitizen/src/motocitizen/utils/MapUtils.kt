package motocitizen.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Medicine
import java.util.*

const val DEFAULT_ZOOM = 16f

private const val DENSE = 1f
private const val SEMI_DENSE = 0.5f
private const val TRANSPARENT = 0.2f

fun GoogleMap.accidentMarker(accident: Accident): Marker = this.addMarker(makeMarker(accident))

private fun calculateAlpha(accident: Accident): Float {
    val age = ((Date().time - accident.time.time) / MS_IN_HOUR).toInt()
    return when {
        age < 2 -> DENSE
        age < 6 -> SEMI_DENSE
        else    -> TRANSPARENT
    }
}

private fun markerTitle(accident: Accident): String {
    val medicine = if (accident.medicine === Medicine.NO) "" else ", " + accident.medicine.text
    val interval = accident.time.getIntervalFromNowInText()
    return "${accident.type.text}$medicine, $interval назад"
}

private fun makeMarker(accident: Accident): MarkerOptions {
    return MarkerOptions()
            .position(accident.coordinates)
            .title(markerTitle(accident))
            .icon(accident.type.icon)
            .alpha(calculateAlpha(accident))
}
