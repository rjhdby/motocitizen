package motocitizen.utils

import motocitizen.content.Content

fun Int.dp(): Int = (dpScale * this + 0.5f).toInt()

fun Int.toKilometers(): Float = (this / 10).toFloat() / 100

fun Int.name() = Content.volunteerName(this)