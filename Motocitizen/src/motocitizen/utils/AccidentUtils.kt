package motocitizen.utils

import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine

fun Accident.isActive(): Boolean = status === AccidentStatus.ACTIVE
fun Accident.isEnded(): Boolean = status !== motocitizen.dictionary.AccidentStatus.ACTIVE
fun Accident.isHidden(): Boolean = status === motocitizen.dictionary.AccidentStatus.HIDDEN
fun Accident.getAccidentTextToCopy(): String {
    val medicineText = if (medicine == Medicine.UNKNOWN) "" else medicine.text + ". "
    return "${time.dateTimeString()} ${ownerName()}: ${type.text}.$medicineText $address. $description."
}

val Accident.latitude
    get() = coordinates.latitude

val Accident.longitude
    get() = coordinates.longitude