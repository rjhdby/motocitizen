package motocitizen.geo.geolocation

import com.google.android.gms.location.LocationRequest

object LocationRequestFactory {
    private val LOW_INTERVAL = 60000L
    private val LOW_BEST = 30000L
    private val LOW_DISPLACEMENT = 200f

    private val HIGH_INTERVAL = 5000L
    private val HIGH_BEST = 1000L
    private val HIGH_DISPLACEMENT = 10f

    fun accurate(): LocationRequest =
            make(HIGH_INTERVAL, HIGH_BEST, HIGH_DISPLACEMENT, LocationRequest.PRIORITY_HIGH_ACCURACY)

    fun coarse(): LocationRequest =
            make(LOW_INTERVAL, LOW_BEST, LOW_DISPLACEMENT, LocationRequest.PRIORITY_LOW_POWER)

    private fun make(interval: Long, bestInterval: Long, displacement: Float, priority: Int): LocationRequest =
            LocationRequest()
                    .setInterval(interval)
                    .setFastestInterval(bestInterval)
                    .setSmallestDisplacement(displacement)
                    .setPriority(priority)
}