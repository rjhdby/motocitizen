package motocitizen.utils

import android.content.Context
import android.util.DisplayMetrics
import motocitizen.MyApp
import org.jetbrains.anko.windowManager

val dpScale: Float by lazy { MyApp.context.resources.displayMetrics.density }

private fun Context.getDisplayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

fun Context.displayWidth(): Int = getDisplayMetrics().widthPixels