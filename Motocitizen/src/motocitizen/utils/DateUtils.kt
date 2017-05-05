@file:JvmName("DateUtils")

package motocitizen.utils

import android.content.Context
import motocitizen.main.R
import java.text.SimpleDateFormat
import java.util.*

fun getTime(date: Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}

fun getDateTime(date: Date): String {
    return SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()).format(date)
}

fun getDbFormat(date: Date): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
}

fun getStringTime(date: Date): String {
    return getDateTime(date)
}

fun getIntervalFromNowInText(context: Context, date: Date): String {
    val minutes = ((Date().time - date.time) / 60000).toInt()
    if (minutes == 0) return "Только что"
    return context.resources.getString(R.string.time_interval_short, minutes / 60, minutes % 60)
}