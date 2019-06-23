package motocitizen.migration

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import motocitizen.MyApp
import motocitizen.utils.tryOrPrintStack

object PreferencesMigration : MigrationInterface {
    lateinit var preferences: SharedPreferences
    override fun process(context: Context) {
        if (MyApp.oldVersion == 0) return
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (MyApp.oldVersion < 996) {
            migrate933to996()
        }
    }

    private fun migrate933to996() {
        stringToInt("hours.ago")
        stringToInt("mc.distance.show")
        stringToInt("mc.distance.alarm")
        stringToInt("notifications.max")

        if (preferences.getString("authType", "") != "") return
        preferences.edit().putString("authType", when {
            preferences.getBoolean("mc.anonim", false)     -> "anon"
            preferences.getString("mc.password", "") != "" -> "forum"
            preferences.getString("vkToken", "") != ""     -> "vk"
            else                                           -> "none"
        }).apply()
    }

    private fun stringToInt(name: String) = tryOrPrintStack {
        val pref = preferences.getString(name, "").toInt()
        preferences.edit().remove(name).apply()
        preferences.edit().putInt(name, pref).apply()
    }
}