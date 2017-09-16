package motocitizen.geolocation

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import motocitizen.geocoder.MyGeoCoder
import java.io.IOException

object AddressResolver {
    fun getAddress(location: LatLng): String {
        try {
            val list = findAddressByLocation(location)
            if (list != null && list.isNotEmpty()) {
                return buildAddressString(list[0])
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun buildAddressString(address: Address): String {
        return StringBuilder()
                .append(extractLocality(address))
                .append(" ")
                .append(extractThoroughfare(address))
                .append(" ")
                .append(if (address.featureName != null) address.featureName else "")
                .toString()
                .trim { it <= ' ' }
    }

    @Throws(IOException::class)
    private fun findAddressByLocation(location: LatLng): List<Address>? =
            MyGeoCoder.getInstance().getFromLocation(location.latitude, location.longitude, 1)

    private fun extractLocality(address: Address): String {
        if (address.locality != null) return address.locality
        if (address.adminArea != null) return address.adminArea
        return if (address.maxAddressLineIndex > 0) address.getAddressLine(0) else ""
    }

    private fun extractThoroughfare(address: Address): String {
        if (address.thoroughfare != null) return address.thoroughfare
        return if (address.subAdminArea != null) address.subAdminArea else ""
    }
}