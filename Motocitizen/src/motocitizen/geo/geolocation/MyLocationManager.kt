package motocitizen.geo.geolocation

import android.app.Activity
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import motocitizen.MyApp
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.requests.InPlaceRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.geocoder.MyGeoCoder
import motocitizen.permissions.Permissions
import motocitizen.subscribe.SubscribeManager
import motocitizen.user.User
import motocitizen.utils.buildAddressString
import motocitizen.utils.distanceTo
import motocitizen.utils.toLatLng
import motocitizen.utils.toLocation

object MyLocationManager {
    private const val ARRIVED_MAX_ACCURACY = 200

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationListener(locationResult?.lastLocation ?: Preferences.savedLatLng.toLocation())
        }
    }

    private fun locationListener(location: Location) {
        Preferences.savedLatLng = location.toLatLng()
        SubscribeManager.fireEvent(SubscribeManager.Event.LOCATION_UPDATED)
        checkInPlace(location)
    }

    fun getLocation(): LatLng = Preferences.savedLatLng

    fun getAddress(location: LatLng = getLocation()) = MyGeoCoder.getFromLocation(location).buildAddressString()

    fun sleep() = runLocationService(LocationRequestFactory.coarse())

    @SuppressWarnings("MissingPermission")
    fun wakeup(context: Activity) {
        Permissions.requestLocation(context) {
            LocationServices.getFusedLocationProviderClient(context)
                    .lastLocation
                    .addOnSuccessListener { if (it != null) Preferences.savedLatLng = it.toLatLng() }
            runLocationService(LocationRequestFactory.accurate())
        }
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
            InPlaceRequest(accident.id).call()
        }
    }

    private fun isArrived(location: Location, accident: Accident): Boolean =
            accident.distanceTo(location) < Math.max(ARRIVED_MAX_ACCURACY, location.accuracy.toInt())

    private fun isInPlace(location: Location, accident: Accident): Boolean =
            accident.distanceTo(location) - location.accuracy < ARRIVED_MAX_ACCURACY

    @SuppressWarnings("MissingPermission")
    private fun runLocationService(locationRequest: LocationRequest) {
        LocationServices.getFusedLocationProviderClient(MyApp.context).apply {
            removeLocationUpdates(locationCallback)
            requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }
}
