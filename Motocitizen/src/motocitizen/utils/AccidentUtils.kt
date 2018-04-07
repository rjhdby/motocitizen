package motocitizen.utils

import android.location.Location
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Medicine

typealias Id = Int

fun Id.name() = Content.volunteerName(this)

fun Accident.getAccidentTextToCopy(): String {
    val medicineText = if (medicine == Medicine.UNKNOWN) "" else medicine.text + ". "
    return "${time.dateTimeString()} ${owner.name()}: ${type.text}.$medicineText $address. $description."
}

val Accident.latitude
    inline get() = coordinates.latitude

val Accident.longitude
    inline get() = coordinates.longitude

fun Accident.distanceString(): String = coordinates.distanceString()

fun Accident.distanceTo(location: Location) = coordinates.distanceTo(location)