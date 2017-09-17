@file:JvmName("DateUtils")

package motocitizen.utils

import android.content.Context
import motocitizen.main.R
import java.text.SimpleDateFormat
import java.util.*

fun Date.timeString(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)

fun Date.dateTimeString(): String = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()).format(this)

fun getIntervalFromNowInText(context: Context, date: Date): String {
    val minutes = ((Date().time - date.time) / 60000).toInt()
    if (minutes == 0) return "Только что"
    return context.resources.getString(R.string.time_interval_short, minutes / 60, minutes % 60)
}