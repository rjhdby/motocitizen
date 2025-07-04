package motocitizen.ui.menus

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.PopupWindow

abstract class ContextMenu(val context: Context) : PopupWindow() {
    private val rootView = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(0xFF202020.toInt())
        layoutParams = LinearLayout.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
    }

    init {
        isOutsideTouchable = true
        contentView = rootView
        width = WRAP_CONTENT
        height = WRAP_CONTENT
    }

    fun addButton(name: String, callback: () -> Unit) = rootView.addView(
            ContextMenuItem(context, name) {
                dismiss()
                callback()
            })

    fun addButton(resource: Int, callback: () -> Unit) = addButton(context.getString(resource), callback)
}