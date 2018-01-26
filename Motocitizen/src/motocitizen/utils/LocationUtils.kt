@file:JvmName("LocationUtils")

package motocitizen.utils

import android.location.Address
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.datasources.network.requests.GeoCoderRequest
import motocitizen.geo.geocoder.MyGeoCoder
import motocitizen.geo.geolocation.MyLocationManager
import org.json.JSONException
import java.io.IOException

const val EQUATOR = 20038

fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)

fun LatLng.distanceTo(latLng: LatLng): Float = distanceTo(latLng.toLocation())

fun LatLng.distanceTo(location: Location): Float = toLocation().distanceTo(location)

fun LatLng.toLocation(): Location {
    val location = Location(LocationManager.GPS_PROVIDER)
    location.latitude = latitude
    location.longitude = longitude
    return location
}

//todo smell
fun String.requestLatLngFromAddress(callback: (LatLng?) -> Unit) {
    try {
        val address = MyGeoCoder.getFromLocationName(this)
        if (address.hasLatitude()) {
            callback(address.latLng)
            return
        }
    } catch (e: IOException) {
    }
    GeoCoderRequest(this) {
        try {
            callback(LatLng(it.resultObject.getDouble("lat"), it.resultObject.getDouble("lng")))
        } catch (e: JSONException) {
            callback(null)
        }
    }
}

fun LatLng.distanceString(): String {
    val meters = metersFromUser()
    return if (meters > 1000) {
        meters.toKilometers().toString() + "км"
    } else {
        meters.toString() + "м"
    }
}

fun LatLng.metersFromUser(): Int = Math.round(distanceTo(MyLocationManager.getLocation()))

fun Address.buildAddressString(): String {
    return StringBuilder()
            .append(locality ?: adminArea ?: getAddressLine(0) ?: "")
            .append(" ")
            .append(thoroughfare ?: subAdminArea ?: "")
            .append(" ")
            .append(featureName ?: "")
            .toString()
            .trim()
}

val Address.latLng
    get() = LatLng(latitude, longitude)