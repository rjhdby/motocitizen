package motocitizen.ui.menus

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button

@SuppressLint("ViewConstructor")
class ContextMenuItem(context: Context, val name: String, val callback: () -> Unit) : Button(context) {
    init {
        text = name
        setOnClickListener { callback() }
    }
}