package motocitizen.utils

fun Int.dp(): Int = (GraphUtils.dpScale * this + 0.5f).toInt()

fun Int.toKilometers(): Float = (this / 10).toFloat() / 100