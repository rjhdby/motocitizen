package motocitizen.geo.geocoder

import android.location.Address
import android.location.Geocoder
import motocitizen.MyApp
import java.util.*

object MyGeoCoder {
    private val geoCoder: Geocoder by lazy { Geocoder(MyApp.context) }

    fun getFromLocation(latitude: Double, longitude: Double): Address {
        val result = geoCoder.getFromLocation(latitude, longitude, 1)
        if (result == null || result.isEmpty()) return Address(Locale.getDefault())
        return result[0]
    }

    fun getFromLocationName(name: String): Address = try {
        val result = geoCoder.getFromLocationName(name, 1)
        if (result == null || result.isEmpty()) Address(Locale.getDefault())
        else result[0]
    } catch (e: Exception) {
        Address(Locale.getDefault())
    }
}
