package motocitizen.ui.dialogs

import android.support.v4.app.FragmentActivity
import android.view.View
import motocitizen.dictionary.Medicine
import motocitizen.main.R

class SelectDamageFrame(val context: FragmentActivity, val callback: (Medicine) -> Unit) {
    private val ROOT_VIEW = R.id.create_people_frame
    private val view = context.findViewById(ROOT_VIEW)

    init {
        intArrayOf(R.id.PEOPLE_OK, R.id.PEOPLE_LIGHT, R.id.PEOPLE_HEAVY, R.id.PEOPLE_LETHAL, R.id.PEOPLE_UNKNOWN)
                .forEach { id -> setListener(id, damageSelectListener(id)) }
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

    private fun damageSelectListener(id: Int): View.OnClickListener = View.OnClickListener { _ -> callback(getSelectedMedicine(id)) }

    private fun getSelectedMedicine(id: Int): Medicine = when (id) {
        R.id.PEOPLE_OK      -> Medicine.NO
        R.id.PEOPLE_LIGHT   -> Medicine.LIGHT
        R.id.PEOPLE_HEAVY   -> Medicine.HEAVY
        R.id.PEOPLE_LETHAL  -> Medicine.LETHAL
        R.id.PEOPLE_UNKNOWN -> Medicine.UNKNOWN
        else                -> Medicine.UNKNOWN
    }
}