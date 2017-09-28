package motocitizen.geo.geocoder

import android.content.Context
import android.location.Address
import android.location.Geocoder

object MyGeoCoder {
    private lateinit var geoCoder: Geocoder

    fun initialize(context: Context) {
        geoCoder = Geocoder(context)
    }
//todo remove null
    fun getFromLocation(latitude: Double, longitude: Double): Address? {
        val result = geoCoder.getFromLocation(latitude, longitude, 1)
        if (result == null || result.isEmpty()) return null
        return result[0]
    }

    fun getFromLocationName(name: String): Address? {
        val result = geoCoder.getFromLocationName(name, 1)
        if (result == null || result.isEmpty()) return null
        return result[0]
    }
}
