package motocitizen.geo.maps

import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Medicine
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.user.User
import motocitizen.utils.*
import java.util.*

class CreateAccidentMap(val fragment: FragmentActivity) {
    private val DEFAULT_ZOOM = 16f
    private val PERMITTED_REGION_COLOR = 0x20FF0000
    private val PERMITTED_REGION_RADIUS = 2000
    private val MS_IN_HOUR = 3600000

    private val MAP_CONTAINER = R.id.create_map_container
    private val SEARCH_INPUT = R.id.SearchEditText
    private val ADDRESS_BUTTON = R.id.ADDRESS
    private val SEARCH_BUTTON = R.id.SEARCH

    private val searchEditText = fragment.findViewById(SEARCH_INPUT) as EditText

    private val initialLocation = MyLocationManager.getLocation()!!
    private lateinit var map: GoogleMap

    init {
        val mapFragment = fragment.supportFragmentManager.findFragmentById(MAP_CONTAINER) as SupportMapFragment
        mapFragment.getMapAsync(mapReadyCallback())
        fragment.findViewById(R.id.SEARCH).setOnClickListener({ fromAddress(searchEditText.text.toString(), searchCallback()) })
    }

    private fun mapReadyCallback(): OnMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation.toLatLng(), DEFAULT_ZOOM))
        setUpMapUi()
        placeAccidentsOnMap()
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

        map.addCircle(CircleOptions()
                              .center(initialLocation.toLatLng())
                              .radius(PERMITTED_REGION_RADIUS.toDouble())
                              .fillColor(PERMITTED_REGION_COLOR))
        map.setOnCameraMoveCanceledListener(cameraMoveCanceledListener())
    }

    private fun enableMyLocation() {
        Permissions.requestLocation(fragment, this::hasLocationPermission)
    }

    private fun hasLocationPermission() {
        map.isMyLocationEnabled = true
    }

    private fun cameraMoveCanceledListener(): GoogleMap.OnCameraMoveCanceledListener {
        return GoogleMap.OnCameraMoveCanceledListener {
            val mcCreateFineAddressConfirm = fragment.findViewById(ADDRESS_BUTTON) as Button
            mcCreateFineAddressConfirm.isEnabled = false
            val distance = map.cameraPosition.target.distanceTo(initialLocation).toDouble()
            mcCreateFineAddressConfirm.isEnabled = distance < PERMITTED_REGION_RADIUS
        }
    }

    private fun placeAccidentsOnMap() {
        map.clear()
        Content.getVisible().forEach { accident -> map.addMarker(makeMarker(accident)) }
    }

    private fun makeMarker(accident: Accident): MarkerOptions {
        return MarkerOptions()
                .position(accident.coordinates)
                .title(makeTitle(accident))
                .icon(accident.type.icon)
                .alpha(calculateAlpha(accident))
    }

    private fun calculateAlpha(accident: Accident): Float {
        val age = ((Date().time - accident.time.time) / MS_IN_HOUR).toInt()
        return when {
            age < 2 -> 1f
            age < 6 -> 0.5f
            else    -> 0.2f
        }
    }

    private fun makeTitle(accident: Accident): String {
        val medicine = if (accident.medicine === Medicine.NO) "" else ", " + accident.medicine.text
        val interval = getIntervalFromNowInText(fragment, accident.time)
        return String.format("%s%s, %s назад",
                             accident.type.text,
                             medicine,
                             interval)
    }

    private fun searchCallback(): Function1<LatLng?, Unit> {
        return { latLng ->
            if (latLng == null) {
                show(fragment, fragment.getString(R.string.nothing_is_found))
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
            }
            Unit
        }
    }

    fun coordinates(): LatLng = map.cameraPosition.target
}