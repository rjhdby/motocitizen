package motocitizen.utils

inline fun <T> tryOr(default: T, work: () -> T): T = try {
    work()
} catch (e: Exception) {
    default
}

inline fun tryOrPrintStack(work: () -> Unit) = try {
    work()
} catch (e: Throwable) {
    e.printStackTrace()
}

inline fun tryOrDo(default: (Exception) -> Unit, work: () -> Unit) = try {
    work()
} catch (e: Exception) {
    default(e)
}