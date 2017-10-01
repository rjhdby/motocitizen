package motocitizen.geo.maps

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Type
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.utils.DEFAULT_ZOOM
import motocitizen.utils.accidentMarker
import java.util.*

class MainMapManager(val fragment: FragmentActivity) {

    private lateinit var map: GoogleMap
    private var mapReady = false
    private var user: Marker? = null
    private var selected: String = ""
    private val accidents = HashMap<String, Int>()

    private val delayedAction = ArrayList<() -> Unit>()

    init {
        jumpToPoint(MyLocationManager.getLocation())
        val mapFragment = SupportMapFragment()
        val fragmentTransaction = fragment.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.google_map, mapFragment, "MAP").commit()
        mapFragment.getMapAsync(this::onMapReady)
    }

    private fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapReady = true
        init(fragment)
        runDelayedJobs()
    }

    private fun runDelayedJobs() {
        delayedAction.forEach { it() }
        delayedAction.clear()
    }

    fun addContent() {
        queryJob {
            map.clear()
            addUserMarker()
            addAccidentsMarkers()
        }
    }

    private fun addAccidentsMarkers() {
        Content.getVisible().forEach(this::addMarker)
    }

    private fun addMarker(accident: Accident) {
        accidents.put(map.accidentMarker(accident).id, accident.id)
    }

    private fun addUserMarker() {
        if (user != null) user!!.remove()
        val location = MyLocationManager.getLocation()
        user = map.addMarker(MarkerOptions().position(location).title(Type.USER.text).icon(Type.USER.icon))
    }

    fun jumpToPoint(latLng: LatLng) {
        queryJob { map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)) }
    }

    private fun init(context: Context) {
        map.clear()
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener { markerClicked(context, it) }
        map.setOnMapLongClickListener { Router.toExternalMap(context as Activity, it) }
    }

    private fun markerClicked(context: Context, marker: Marker): Boolean {
        val id = marker.id
        if (selected == id && accidents.containsKey(id)) {
            toDetails(context, accidents[id]!!)
        } else {
            marker.showInfoWindow()
            selected = id
        }
        return true
    }

    fun enableLocation() {
        queryJob { map.isMyLocationEnabled = true }
    }

    private fun queryJob(job: () -> Unit) {
        if (mapReady) job()
        else delayedAction.add { job() }
    }

    private fun toDetails(context: Context, id: Int) {
        val bundle = Bundle()
        bundle.putInt(AccidentDetailsActivity.ACCIDENT_ID_KEY, id)
        Router.goTo(context as Activity, Router.Target.DETAILS, bundle)
    }
}
