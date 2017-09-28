package motocitizen.geo.geolocation

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.requests.InPlaceRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.MyGoogleApiClient
import motocitizen.geo.geocoder.AddressResolver
import motocitizen.user.User
import motocitizen.utils.distanceTo
import motocitizen.utils.toLatLng

object MyLocationManager {
    private val ARRIVED_MAX_ACCURACY = 200

    private val subscribers = HashMap<String, (LatLng) -> Unit>()

    private fun locationListener(location: Location) {
        Preferences.savedLatLng = LatLng(location.latitude, location.longitude)
//        requestAddress()
        subscribers.values.forEach { it(location.toLatLng()) }
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

    fun subscribeToLocationUpdate(name: String, callback: (LatLng) -> Unit) {
        subscribers.put(name, callback)
    }

    fun unSubscribe(name: String) {
        subscribers.remove(name)
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
        return accident.coordinates.distanceTo(location) < Math.max(ARRIVED_MAX_ACCURACY, location.accuracy.toInt())
    }

    private fun isInPlace(location: Location, accident: Accident): Boolean {
        return accident.coordinates.distanceTo(location) - location.accuracy < ARRIVED_MAX_ACCURACY
    }
}
