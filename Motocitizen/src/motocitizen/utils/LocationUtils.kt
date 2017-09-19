@file:JvmName("LocationUtils")

package motocitizen.utils

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.datasources.network.requests.GeoCoderRequest
import motocitizen.geo.geocoder.MyGeoCoder
import motocitizen.geo.geolocation.MyLocationManager
import org.json.JSONException
import java.io.IOException

val EQUATOR = 20038

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)

fun LatLng.distanceTo(latLng: LatLng): Float = distanceTo(latLng.toLocation())

fun LatLng.distanceTo(location: Location): Float = this.toLocation().distanceTo(location)

fun LatLng.toLocation(): Location {
    val location = Location(LocationManager.GPS_PROVIDER)
    location.latitude = this.latitude
    location.longitude = this.longitude
    return location
}

fun fromAddress(name: String, callback: (LatLng?) -> Unit) {
    try {
        val address = MyGeoCoder.getFromLocationName(name)
        if (address != null) {
            callback(LatLng(address.latitude, address.longitude))
            return
        }
    } catch (e: IOException) {
    }
    GeoCoderRequest(name, callback = { response ->
        try {
            val location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
            callback(LatLng(location.getDouble("lat"), location.getDouble("lng")))
        } catch (e: JSONException) {
            callback(null)
        }
    })
}

fun distanceString(latLng: LatLng): String {
    val meters = metersFromUser(latLng)
    return if (meters > 1000) {
        ((meters / 10).toFloat() / 100).toString() + "км"
    } else {
        meters.toString() + "м"
    }
}

fun metersFromUser(latLng: LatLng): Int = Math.round(latLng.distanceTo(MyLocationManager.getLocation()))