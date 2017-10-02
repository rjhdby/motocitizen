package motocitizen.utils

data class MarginArray(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    private val _values = arrayOf(left, top, right, bottom)

    operator fun get(i: Int): Int = _values[i]
}
