package motocitizen.geo.maps

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Type
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.ui.activity.AccidentDetailsActivity

class MainMapManager(fragment: FragmentActivity) : MapManager(fragment, R.id.google_map) {
    override fun onMapReady() {
        map.setOnMarkerClickListener { markerClicked(fragment, it) }
        map.setOnMapLongClickListener { Router.toExternalMap(fragment as Activity, it) }
    }

    override fun update() {
        addContent { addUserMarker() }
    }

    private var user: Marker? = null
    private var selected: String = ""

    private fun addUserMarker() {
        if (user != null) user!!.remove()
        val location = MyLocationManager.getLocation()
        user = map.addMarker(MarkerOptions().position(location).title(Type.USER.text).icon(Type.USER.icon))
    }

    private fun markerClicked(context: Context, marker: Marker): Boolean {
        val id = marker.id
        if (selected == id && markers.containsKey(id)) {
            toDetails(context, markers[id]!!)
        } else {
            marker.showInfoWindow()
            selected = id
        }
        return true
    }

    private fun toDetails(context: Context, id: Int) {
        val bundle = Bundle()
        bundle.putInt(AccidentDetailsActivity.ACCIDENT_ID_KEY, id)
        Router.goTo(context as Activity, Router.Target.DETAILS, bundle)
    }

    fun centerOnAccident(accident: Accident) {
        centerOn(accident.coordinates)
    }
}
