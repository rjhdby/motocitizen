package motocitizen.dictionary

interface Dictionary<out T> {
    val code: T
    val text: String
}