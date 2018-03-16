package motocitizen.ui.frames.create

import android.support.v4.app.FragmentActivity
import android.view.View
import motocitizen.dictionary.Type
import motocitizen.main.R
import motocitizen.ui.frames.FrameInterface
import motocitizen.utils.hide
import motocitizen.utils.show

class TypeFrame(val context: FragmentActivity, val callback: (Type) -> Unit) : FrameInterface {
    private val view = context.findViewById<View>(R.id.create_type_frame)

    init {
        intArrayOf(R.id.ACCIDENT, R.id.BREAK, R.id.STEAL, R.id.OTHER)
                .forEach { setListener(it, typeSelectListener(it)) }
    }

    override fun show() = view.show()

    override fun hide() = view.hide()

    private fun setListener(id: Int, listener: View.OnClickListener) {
        context.findViewById<View>(id).setOnClickListener(listener)
    }

    private fun typeSelectListener(id: Int): View.OnClickListener = View.OnClickListener { callback(getSelectedType(id)) }

    private fun getSelectedType(id: Int): Type = when (id) {
        R.id.ACCIDENT -> Type.MOTO_AUTO
        R.id.BREAK    -> Type.BREAK
        R.id.STEAL    -> Type.STEAL
        R.id.OTHER    -> Type.OTHER
        else          -> Type.OTHER
    }
}