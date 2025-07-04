package motocitizen.geo.maps

import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import motocitizen.geo.geocoder.MyGeoCoder
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.user.User
import motocitizen.utils.distanceTo
import motocitizen.utils.gone
import motocitizen.utils.showToast

class CreateAccidentMap(fragment: FragmentActivity) : MapManager(fragment, R.id.create_map_container) {
    companion object {
        private const val PERMITTED_REGION_COLOR = 0x20FF0000
        private const val PERMITTED_REGION_RADIUS = 2000
    }

    override fun onMapReady() {
        addMapConstraints()
        fragment.findViewById<ImageButton>(R.id.SEARCH)
                .setOnClickListener {
                    MyGeoCoder.latLngFromAddress(searchEditText.text.toString()) { searchCallback(it) }
                }
    }

    override fun update() = addContent { }

    private val searchEditText: EditText = fragment.findViewById(R.id.SearchEditText)
    private val mcCreateFineAddressConfirm: Button = fragment.findViewById(R.id.ADDRESS)
    private val searchButton: ImageButton = fragment.findViewById(R.id.SEARCH)

    private val initialLocation = MyLocationManager.getLocation()

    private fun searchCallback(latLng: LatLng?) = when (latLng) {
        null -> fragment.showToast(R.string.nothing_is_found)
        else -> centerOn(latLng)
    }

    private fun addMapConstraints() {
        if (User.isModerator()) return

        //Прячем кнопки поиска адреса
        searchEditText.gone()
        searchButton.gone()

        map.addCircle(permittedCircle())
        map.setOnCameraMoveCanceledListener(this::cameraMoveCanceledListener)
    }

    private fun permittedCircle() = CircleOptions()
            .center(initialLocation)
            .radius(PERMITTED_REGION_RADIUS.toDouble())
            .fillColor(PERMITTED_REGION_COLOR)

    //todo ???
    private fun cameraMoveCanceledListener() {
        mcCreateFineAddressConfirm.isEnabled = false
        val distance = map.cameraPosition.target.distanceTo(initialLocation).toDouble()
        mcCreateFineAddressConfirm.isEnabled = distance < PERMITTED_REGION_RADIUS
    }

    fun coordinates(): LatLng = map.cameraPosition.target
}