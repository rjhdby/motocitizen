package motocitizen.utils

import android.content.Context
import android.util.DisplayMetrics
import org.jetbrains.anko.windowManager

object GraphUtils {
    var displayWidth: Int = 0
    var dpScale: Float = 1f

    fun initialize(context: Context) {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayWidth = displayMetrics.widthPixels
        dpScale = context.resources.displayMetrics.density
    }
}

fun Int.dp(): Int = (GraphUtils.dpScale * this + 0.5f).toInt()
