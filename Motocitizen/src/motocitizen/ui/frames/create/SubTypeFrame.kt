package motocitizen.ui.frames.create

import android.support.v4.app.FragmentActivity
import android.view.View
import motocitizen.dictionary.Type
import motocitizen.main.R
import motocitizen.ui.frames.FrameInterface

class SubTypeFrame(val context: FragmentActivity, val callback: (Type) -> Unit) : FrameInterface {
    private val ROOT_VIEW = R.id.create_acc_frame
    private val view = context.findViewById<View>(ROOT_VIEW)

    init {
        intArrayOf(R.id.MOTO_AUTO, R.id.SOLO, R.id.MOTO_MOTO, R.id.MOTO_MAN)
                .forEach { id -> setListener(id, typeSelectListener(id)) }
    }

    override fun show() {
        view.visibility = View.VISIBLE
    }

    override fun hide() {
        view.visibility = View.INVISIBLE
    }

    private fun setListener(id: Int, listener: View.OnClickListener) {
        context.findViewById<View>(id).setOnClickListener(listener)
    }

    private fun typeSelectListener(id: Int): View.OnClickListener = View.OnClickListener { _ -> callback(getSelectedType(id)) }

    private fun getSelectedType(id: Int): Type = when (id) {
        R.id.SOLO      -> Type.SOLO
        R.id.MOTO_MOTO -> Type.MOTO_MOTO
        R.id.MOTO_MAN  -> Type.MOTO_MAN
        R.id.MOTO_AUTO -> Type.MOTO_AUTO
        else           -> Type.MOTO_AUTO
    }
}