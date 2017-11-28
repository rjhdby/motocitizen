package motocitizen.utils

import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine

fun Accident.isActive(): Boolean = status === AccidentStatus.ACTIVE
fun Accident.isEnded(): Boolean = !isActive()
fun Accident.isHidden(): Boolean = status === AccidentStatus.HIDDEN
fun Accident.getAccidentTextToCopy(): String {
    val medicineText = if (medicine == Medicine.UNKNOWN) "" else medicine.text + ". "
    return "${time.dateTimeString()} ${owner.name()}: ${type.text}.$medicineText $address. $description."
}

val Accident.latitude
    get() = coordinates.latitude

val Accident.longitude
    get() = coordinates.longitude

fun Accident.distanceString(): String = coordinates.distanceString()