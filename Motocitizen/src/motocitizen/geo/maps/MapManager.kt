package motocitizen.geo.maps

import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.permissions.Permissions
import motocitizen.utils.DEFAULT_ZOOM
import motocitizen.utils.accidentMarker
import java.util.*

abstract class MapManager(protected val fragment: FragmentActivity, mapContainer: Int) {
    private val delayedAction = ArrayList<() -> Unit>()
    protected val markers = HashMap<String, Int>()
    protected lateinit var map: GoogleMap
    private var mapReady = false

    abstract fun update()
    abstract fun onMapReady()

    init {
        val mapFragment = fragment.supportFragmentManager.findFragmentById(mapContainer) as SupportMapFragment
        mapFragment.getMapAsync { mapReadyCallback(it) }
    }

    protected fun addContent(callback: () -> Unit) = queryJob {
        map.clear()
        addAccidentsMarkers()
        callback()
    }

    private fun queryJob(job: () -> Unit) {
        when {
            mapReady -> job()
            else     -> delayedAction.add { job() }
        }
    }

    private fun runDelayedJobs() {
        delayedAction.forEach { it() }
        delayedAction.clear()
    }

    private fun addAccidentsMarkers() {
        Content.getVisible().forEach(this::addMarker)
    }

    private fun addMarker(accident: Accident) {
        markers[map.accidentMarker(accident).id] = accident.id
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        Permissions.requestLocation(fragment, { map.isMyLocationEnabled = true }, {})
    }

    private fun mapReadyCallback(googleMap: GoogleMap) {
        mapReady = true
        map = googleMap
        map.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isZoomControlsEnabled = true
        }
        centerOn(MyLocationManager.getLocation())
        enableLocation()
        update()
        onMapReady()
        runDelayedJobs()
    }

    protected fun centerOn(latLng: LatLng, zoom: Float = DEFAULT_ZOOM) {
        queryJob { map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom)) }
    }
}