package motocitizen.geo.geocoder

import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import motocitizen.MyApp
import motocitizen.datasources.network.requests.GeoCoderRequest
import motocitizen.utils.latLng
import motocitizen.utils.tryOr
import motocitizen.utils.tryOrDo
import java.util.*

object MyGeoCoder {
    private val geoCoder: Geocoder by lazy { Geocoder(MyApp.context) }

    fun getFromLocation(location: LatLng): Address = getAddress { geoCoder.getFromLocation(location.latitude, location.longitude, 1) }

    fun getFromLocationName(name: String): Address = getAddress { geoCoder.getFromLocationName(name, 1) }

    fun latLngFromAddress(name: String, callback: (LatLng?) -> Unit) {
        val address = getFromLocationName(name)
        if (address.hasLatitude()) {
            callback(address.latLng)
            return
        }

        GeoCoderRequest(name) {
            tryOrDo({ callback(null) }) {
                callback(LatLng(it.resultObject.getDouble("lat"), it.resultObject.getDouble("lng")))
            }
        }.call()
    }

    private fun getAddress(source: () -> List<Address>?): Address {
        val result = makeRequest(source)
        return if (result == null || result.isEmpty()) Address(Locale.getDefault())
        else result[0]
    }

    private fun makeRequest(source: () -> List<Address>?) = tryOr(null) { source() }
}
