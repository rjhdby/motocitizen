package motocitizen.ui.menus

import android.content.Context
import android.widget.LinearLayout
import android.widget.PopupWindow
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

abstract class ContextMenu(val context: Context) : PopupWindow() {
    private val rootView = context.verticalLayout {
        backgroundColor = 0xFF202020.toInt()
        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
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

    fun addButton(name: Int, callback: () -> Unit) = addButton(context.getString(name), callback)
}