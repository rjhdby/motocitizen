@file:JvmName("LocationUtils")

package motocitizen.utils

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

public val EQUATOR = 20038

fun distance(l1: Location, l2: LatLng): Float {
    return l1.distanceTo(LatLng2Location(l2))
}

fun LatLng2Location(latLng: LatLng): Location {
    val location = Location(LocationManager.GPS_PROVIDER)
    location.latitude = latLng.latitude
    location.longitude = latLng.longitude
    return location
}

fun Location2LatLng(location: Location): LatLng {
    return LatLng(location.latitude, location.longitude)
}