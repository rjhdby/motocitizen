@file:JvmName("LocationUtils")

package motocitizen.utils

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.geocoder.MyGeoCoder
import motocitizen.network.requests.GeoCoderRequest
import org.json.JSONException
import java.io.IOException

val EQUATOR = 20038

fun distance(l1: Location, l2: LatLng): Float = l1.distanceTo(LatLng2Location(l2))

fun LatLng2Location(latLng: LatLng): Location {
    val location = Location(LocationManager.GPS_PROVIDER)
    location.latitude = latLng.latitude
    location.longitude = latLng.longitude
    return location
}

fun Location2LatLng(location: Location): LatLng = LatLng(location.latitude, location.longitude)

fun LatLngByAddress(address: String, callback: (LatLng?) -> Unit) {
    try {
        val addresses = MyGeoCoder.getInstance().getFromLocationName(address, 1)
        if (addresses.size > 0) {
            callback(LatLng(addresses[0].latitude, addresses[0].longitude))
            return
        }
    } catch (e: IOException) {
    }
    GeoCoderRequest(address, callback = { response ->
        try {
            val location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
            callback(LatLng(location.getDouble("lat"), location.getDouble("lng")))
        } catch (e: JSONException) {
            callback(null)
        }
    })
}