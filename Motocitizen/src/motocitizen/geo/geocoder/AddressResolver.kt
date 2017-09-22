package motocitizen.geo.geocoder

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import motocitizen.utils.buildAddressString
import java.io.IOException

object AddressResolver {

    fun getAddress(location: LatLng): String {
        var address: Address? = null
        try {
            address = findAddressByLocation(location)
        } catch (e: IOException) {
        }
        return address?.buildAddressString() ?: ""
    }

    @Throws(IOException::class)
    private fun findAddressByLocation(location: LatLng): Address? =
            MyGeoCoder.getFromLocation(location.latitude, location.longitude)
}