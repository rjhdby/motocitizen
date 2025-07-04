package motocitizen.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import motocitizen.MyApp

val displayMetrics: DisplayMetrics by lazy { MyApp.context.resources.displayMetrics }

fun Int.dp(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        displayMetrics
    ).toInt()
}

fun Context.displayWidth(): Int = displayMetrics.widthPixels