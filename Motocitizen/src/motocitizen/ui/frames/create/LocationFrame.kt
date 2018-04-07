package motocitizen.ui.frames.create

import android.support.v4.app.FragmentActivity
import android.view.View
import com.google.android.gms.maps.model.LatLng
import motocitizen.geo.maps.CreateAccidentMap
import motocitizen.main.R
import motocitizen.ui.frames.FrameInterface
import motocitizen.utils.hide
import motocitizen.utils.show

class LocationFrame(val context: FragmentActivity, val callback: (LatLng) -> Unit) : FrameInterface {
    private var map: CreateAccidentMap = CreateAccidentMap(context)
    private val view = context.findViewById<View>(R.id.create_map)

    init {
        context.findViewById<View>(R.id.ADDRESS).setOnClickListener(addressSelectListener())
    }

    override fun show() = view.show()

    override fun hide() = view.hide()

    private fun addressSelectListener() = View.OnClickListener { callback(map.coordinates()) }
}