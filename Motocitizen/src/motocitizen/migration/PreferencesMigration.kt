package motocitizen.migration

import android.content.Context
import android.content.SharedPreferences
import motocitizen.datasources.preferences.Preferences

object PreferencesMigration : MigrationInterface {
    lateinit var preferences: SharedPreferences
    override fun process(context: Context) {
        if (Preferences.oldVersion == Preferences.appVersion) return

        if (Preferences.oldVersion < 994) {
            migrate933to994()
        }
    }

    private fun migrate933to994() {
        stringToInt("hours.ago")
        stringToInt("mc.distance.show")
        stringToInt("mc.distance.alarm")
        stringToInt("notifications.max")
        if (Preferences.authType != "none") return
        Preferences.authType = when {
            Preferences.anonymous       -> "anon"
            Preferences.password !== "" -> "forum"
            Preferences.vkToken !== ""  -> "vk"
            else                        -> "none"
        }
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