package motocitizen.utils

import android.content.Context
import android.util.DisplayMetrics
import org.jetbrains.anko.windowManager

object GraphUtils {
    var dpScale: Float = 1f

    fun initialize(context: Context) {
        dpScale = context.resources.displayMetrics.density
    }
}
