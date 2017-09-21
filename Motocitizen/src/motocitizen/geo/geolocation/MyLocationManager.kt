package motocitizen.geo.geolocation

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.requests.InPlaceRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.MyGoogleApiClient
import motocitizen.geo.geocoder.AddressResolver
import motocitizen.ui.activity.MainScreenActivity
import motocitizen.user.User
import motocitizen.utils.distanceTo

object MyLocationManager {
    private val ARRIVED_MAX_ACCURACY = 200

    private fun locationListener(location: Location) {
        Preferences.savedLatLng = LatLng(location.latitude, location.longitude)
        requestAddress()
        checkInPlace(location)
    }

    fun getLocation(): LatLng = MyGoogleApiClient.getLastLocation()

    fun getAddress(location: LatLng): String = AddressResolver.getAddress(location)

    fun sleep() {
        MyGoogleApiClient.runLocationService(LocationRequestFactory.coarse()) { location: Location -> locationListener(location) }
    }

    fun wakeup() {
        MyGoogleApiClient.runLocationService(LocationRequestFactory.accurate()) { location: Location -> locationListener(location) }
    }

    //todo exterminatus
    private fun requestAddress() {
        MainScreenActivity.updateStatusBar(getAddress(getLocation()))
    }

    private fun checkInPlace(location: Location) {
        if (User.name == "") return
        val currentInPlace = Content.inPlace ?: return

        if (isInPlace(location, currentInPlace)) return
        //            Content.INSTANCE.setLeave(currentInPlace); //todo
        //        new LeaveRequest(currentInPlace, (result) -> Unit.INSTANCE);

        val list = Content.getByFilter { accident -> accident === currentInPlace }
        if (list.isEmpty()) return
        val accident = list[0]
        if (isArrived(location, accident)) {
            Content.inPlace = accident
            InPlaceRequest(accident)
        }
    }

    private fun isArrived(location: Location, accident: Accident): Boolean {
        return accident.coordinates.distanceTo(location) < Math.max(ARRIVED_MAX_ACCURACY.toFloat(), location.accuracy)
    }

    private fun isInPlace(location: Location?, accident: Accident): Boolean {
        return location != null && accident.coordinates.distanceTo(location) - location.accuracy < ARRIVED_MAX_ACCURACY
    }
}
