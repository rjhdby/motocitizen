package motocitizen.geo.maps

import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.user.User
import motocitizen.utils.distanceTo
import motocitizen.utils.requestLatLngFromAddress
import motocitizen.utils.showToast

class CreateAccidentMap(fragment: FragmentActivity) : MapManager(fragment, R.id.create_map_container) {
    override fun onMapReady() {
        addMapConstraints()
        fragment.findViewById(R.id.SEARCH)
                .setOnClickListener({ searchEditText.text.toString().requestLatLngFromAddress { searchCallback(it) } })
    }

    override fun update() {
        addContent { }
    }

    private val PERMITTED_REGION_COLOR = 0x20FF0000
    private val PERMITTED_REGION_RADIUS = 2000

    private val searchEditText = fragment.findViewById(R.id.SearchEditText) as EditText
    private val mcCreateFineAddressConfirm = fragment.findViewById(R.id.ADDRESS) as Button
    private val searchButton = fragment.findViewById(R.id.SEARCH) as ImageButton

    private val initialLocation = MyLocationManager.getLocation()

    private fun searchCallback(latLng: LatLng?) {
        if (latLng == null) {
            fragment.showToast(R.string.nothing_is_found)
        } else {
            centerOn(latLng)
        }
    }

    private fun addMapConstraints() {
        if (User.isModerator) return

        //Прячем кнопки поиска адреса
        searchEditText.visibility = View.GONE

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

    private fun cameraMoveCanceledListener() {
        mcCreateFineAddressConfirm.isEnabled = false
        val distance = map.cameraPosition.target.distanceTo(initialLocation).toDouble()
        mcCreateFineAddressConfirm.isEnabled = distance < PERMITTED_REGION_RADIUS
    }

    fun coordinates(): LatLng = map.cameraPosition.target
}