package motocitizen.ui.menus

import android.content.Context
import android.widget.PopupWindow
import motocitizen.utils.matchWrapParams
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

abstract class ContextMenu(val context: Context) : PopupWindow() {
    private val rootView = context.verticalLayout {
        backgroundColor = 0xFF202020.toInt()
        layoutParams = matchWrapParams
    }

    init {
        isOutsideTouchable = true
        contentView = rootView
        width = wrapContent
        height = wrapContent
    }

    fun addButton(name: String, callback: () -> Unit) {
        rootView.addView(ContextMenuItem(context, name, {
            dismiss()
            callback()
        }))
    }

    fun addButton(resource: Int, callback: () -> Unit) = addButton(context.getString(resource), callback)
}