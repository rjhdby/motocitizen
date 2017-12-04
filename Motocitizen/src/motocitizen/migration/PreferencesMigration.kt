package motocitizen.migration

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PreferencesMigration : MigrationInterface {
    lateinit var preferences: SharedPreferences
    override fun process(context: Context, old: Int, new: Int) {
//        if (old == new) return
        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        stringToInt("hours.ago")
        stringToInt("mc.distance.show")
        stringToInt("mc.distance.alarm")
        stringToInt("notifications.max")
    }

    private fun stringToInt(name: String) {
        try {
            val pref = preferences.getString(name, "").toInt()
            preferences.edit().remove(name).apply()
            preferences.edit().putInt(name, pref).apply()
        } catch (e: Exception) {
        }
    }
}