package motocitizen.ui.menus

import android.content.Context
import android.widget.Button

class ContextMenuItem(context: Context, val name: String, val callback: () -> Unit) : Button(context) {
    init {
        text = name
        setOnClickListener { callback() }
    }
}