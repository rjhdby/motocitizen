package motocitizen.utils

import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine

fun Accident.isActive(): Boolean = status === AccidentStatus.ACTIVE
fun Accident.isEnded(): Boolean = status !== motocitizen.dictionary.AccidentStatus.ACTIVE
fun Accident.isHidden(): Boolean = status === motocitizen.dictionary.AccidentStatus.HIDDEN
fun Accident.getAccidentTextToCopy(): String {
    val res = StringBuilder()
    res.append(this.time.dateTimeString()).append(" ")
    res.append(this.ownerName()).append(": ")
    res.append(this.type.text).append(". ")
    if (this.medicine !== Medicine.UNKNOWN) {
        res.append(this.medicine.text).append(". ")
    }
    res.append(this.address).append(". ")
    res.append(this.description).append(".")
    return res.toString()
}