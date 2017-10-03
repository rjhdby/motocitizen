package motocitizen.geo.maps

import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.user.User
import motocitizen.utils.*

class CreateAccidentMap(val fragment: FragmentActivity) {

    private val PERMITTED_REGION_COLOR = 0x20FF0000
    private val PERMITTED_REGION_RADIUS = 2000

    private val MAP_CONTAINER = R.id.create_map_container
    private val SEARCH_INPUT = R.id.SearchEditText
    private val ADDRESS_BUTTON = R.id.ADDRESS
    private val SEARCH_BUTTON = R.id.SEARCH

    private val searchEditText = fragment.findViewById(SEARCH_INPUT) as EditText

    private val initialLocation = MyLocationManager.getLocation()
    private lateinit var map: GoogleMap

    init {
        val mapFragment = fragment.supportFragmentManager.findFragmentById(MAP_CONTAINER) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReadyCallback)
        fragment.findViewById(R.id.SEARCH).setOnClickListener({ fromAddress(searchEditText.text.toString(), this::searchCallback) })
    }

    private fun mapReadyCallback(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, DEFAULT_ZOOM))
        setUpMapUi()
        placeAccidentsOnMap()
    }

    private fun searchCallback(latLng: LatLng?) {
        if (latLng == null) {
            fragment.showToast(R.string.nothing_is_found)
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
        }
    }

    private fun setUpMapUi() {
        enableMyLocation()
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        addMapConstraints()
    }

    private fun addMapConstraints() {
        if (User.isModerator) return

        //Прячем кнопки поиска адреса
        searchEditText.visibility = View.GONE
        val searchButton = fragment.findViewById(SEARCH_BUTTON) as ImageButton
        searchButton.visibility = View.GONE

        map.addCircle(permittedCircle())
        map.setOnCameraMoveCanceledListener(this::cameraMoveCanceledListener)
    }

    private fun permittedCircle(): CircleOptions {
        return CircleOptions()
                .center(initialLocation)
                .radius(PERMITTED_REGION_RADIUS.toDouble())
                .fillColor(PERMITTED_REGION_COLOR)
    }

    private fun enableMyLocation() {
        Permissions.requestLocation(fragment, this::hasLocationPermission)
    }

    private fun hasLocationPermission() {
        map.isMyLocationEnabled = true
    }

    private fun cameraMoveCanceledListener() {
        val mcCreateFineAddressConfirm = fragment.findViewById(ADDRESS_BUTTON) as Button
        mcCreateFineAddressConfirm.isEnabled = false
        val distance = map.cameraPosition.target.distanceTo(initialLocation).toDouble()
        mcCreateFineAddressConfirm.isEnabled = distance < PERMITTED_REGION_RADIUS
    }

    private fun placeAccidentsOnMap() {
        map.clear()
        Content.getVisible().forEach { map.accidentMarker(it) }
    }

    fun coordinates(): LatLng = map.cameraPosition.target
}