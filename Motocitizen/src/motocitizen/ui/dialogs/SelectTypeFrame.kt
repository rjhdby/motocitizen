package motocitizen.ui.dialogs

import android.support.v4.app.FragmentActivity
import android.view.View
import motocitizen.dictionary.Type
import motocitizen.main.R

class SelectTypeFrame(val context: FragmentActivity, val callback: (Type) -> Unit) {
    private val ROOT_VIEW = R.id.create_type_frame
    private val view = context.findViewById(ROOT_VIEW)

    init {
        intArrayOf(R.id.ACCIDENT, R.id.BREAK, R.id.STEAL, R.id.OTHER)
                .forEach { id -> setListener(id, typeSelectListener(id)) }
    }

    fun show() {
        view.visibility = View.VISIBLE
    }

    fun hide() {
        view.visibility = View.INVISIBLE
    }

    private fun setListener(id: Int, listener: View.OnClickListener) {
        context.findViewById(id).setOnClickListener(listener)
    }

    private fun typeSelectListener(id: Int): View.OnClickListener = View.OnClickListener { _ -> callback(getSelectedType(id)) }

    private fun getSelectedType(id: Int): Type = when (id) {
        R.id.ACCIDENT -> Type.MOTO_AUTO
        R.id.BREAK    -> Type.BREAK
        R.id.STEAL    -> Type.STEAL
        R.id.OTHER    -> Type.OTHER
        else          -> Type.OTHER
    }
}