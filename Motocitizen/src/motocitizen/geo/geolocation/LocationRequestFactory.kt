package motocitizen.geo.geolocation

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority

object LocationRequestFactory {
    private const val LOW_INTERVAL = 120000L
    private const val LOW_BEST = 30000L
    private const val LOW_DISPLACEMENT = 400f

    private const val HIGH_INTERVAL = 10000L
    private const val HIGH_BEST = 1000L
    private const val HIGH_DISPLACEMENT = 10f

    fun accurate(): LocationRequest = make(HIGH_INTERVAL, HIGH_BEST, HIGH_DISPLACEMENT, Priority.PRIORITY_HIGH_ACCURACY)

    fun coarse(): LocationRequest = make(LOW_INTERVAL, LOW_BEST, LOW_DISPLACEMENT, Priority.PRIORITY_LOW_POWER)

    private fun make(interval: Long, bestInterval: Long, displacement: Float, priority: Int) = LocationRequest
        .Builder(priority, interval)
        .setMinUpdateIntervalMillis(bestInterval)
        .setMinUpdateDistanceMeters(displacement)
        .build()
}