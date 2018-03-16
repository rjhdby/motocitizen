package motocitizen.ui.frames.create

import android.support.v4.app.FragmentActivity
import android.view.View
import motocitizen.dictionary.Type
import motocitizen.main.R
import motocitizen.ui.frames.FrameInterface
import motocitizen.utils.hide
import motocitizen.utils.show

class SubTypeFrame(val context: FragmentActivity, val callback: (Type) -> Unit) : FrameInterface {
    private val view = context.findViewById<View>(R.id.create_acc_frame)

    init {
        intArrayOf(R.id.MOTO_AUTO, R.id.SOLO, R.id.MOTO_MOTO, R.id.MOTO_MAN)
                .forEach { setListener(it, typeSelectListener(it)) }
    }

    override fun show() = view.show()

    override fun hide() = view.hide()

    private fun setListener(id: Int, listener: View.OnClickListener) {
        context.findViewById<View>(id).setOnClickListener(listener)
    }

    private fun typeSelectListener(id: Int) = View.OnClickListener { callback(getSelectedType(id)) }

    private fun getSelectedType(id: Int): Type = when (id) {
        R.id.SOLO      -> Type.SOLO
        R.id.MOTO_MOTO -> Type.MOTO_MOTO
        R.id.MOTO_MAN  -> Type.MOTO_MAN
        R.id.MOTO_AUTO -> Type.MOTO_AUTO
        else           -> Type.MOTO_AUTO
    }
}