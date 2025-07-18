package motocitizen.geo.maps

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Type
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.utils.toExternalMap

class MainMapManager(fragment: FragmentActivity) : MapManager(fragment, R.id.google_map) {
    override fun onMapReady() {
        map.setOnMarkerClickListener { markerClicked(it) }
        map.setOnMapLongClickListener { (fragment as Activity).toExternalMap(it) }
    }

    override fun update() = addContent { addUserMarker() }

    private var user: Marker? = null
    private var selected: String = ""

    private fun addUserMarker() {
        user?.remove()
        val location = MyLocationManager.getLocation()
        user = map.addMarker(MarkerOptions().position(location).title(Type.USER.text).icon(Type.USER.icon))
    }

    private fun markerClicked(marker: Marker): Boolean {
        val id = marker.id
        marker.showInfoWindow()
        selected = id
        return true
    }

    fun centerOnAccident(accident: Accident) = centerOn(accident.coordinates)
}
