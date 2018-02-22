package motocitizen.utils

import motocitizen.content.Content

typealias Meter = Int
typealias Id = Int
typealias Kilometer = Float

fun Int.dp(): Int = (dpScale * this + 0.5f).toInt()

fun Meter.toKilometers(): Kilometer = (this / 10).toFloat() / 100

//todo OMG!
fun Id.name() = Content.volunteerName(this)