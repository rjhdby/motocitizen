package motocitizen.ui.dialogs

import android.support.v4.app.FragmentActivity
import android.view.View
import com.google.android.gms.maps.model.LatLng
import motocitizen.main.R
import motocitizen.maps.google.CreateAccidentMap

class SelectLocationFrame(val context: FragmentActivity, val callback: (LatLng) -> Unit) {
    private val ROOT_VIEW = R.id.create_map
    private var map: CreateAccidentMap = CreateAccidentMap(context)
    private val view = context.findViewById(ROOT_VIEW)

    init {
        context.findViewById(R.id.ADDRESS).setOnClickListener(addressSelectListener())
    }

    fun show() {
        view.visibility = View.VISIBLE
    }

    fun hide() {
        view.visibility = View.INVISIBLE
    }

    private fun addressSelectListener(): View.OnClickListener = View.OnClickListener { _ -> callback(map.coordinates()) }
}