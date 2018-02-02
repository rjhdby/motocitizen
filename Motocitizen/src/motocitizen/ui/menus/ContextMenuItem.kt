package motocitizen.ui.menus

import android.content.Context
import android.support.v7.widget.AppCompatButton

class ContextMenuItem(context: Context, val name: String, val callback: () -> Unit) : AppCompatButton(context) {
    init {
        text = name
        setOnClickListener { callback() }
    }
}