package motocitizen.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager

import com.google.android.gms.maps.model.LatLng

import motocitizen.dictionary.Type

class Preferences private constructor(context: Context) {
    var newVersion = false
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var showAcc
        get() = preferences.getBoolean("mc.show.acc", DEFAULT_SHOW_TYPE)
        set(value) = preferences.edit().putBoolean("mc.show.acc", value).apply()
    var showBreak
        get() = preferences.getBoolean("mc.show.break", DEFAULT_SHOW_TYPE)
        set(value) = preferences.edit().putBoolean("mc.show.break", value).apply()
    var showSteal
        get() = preferences.getBoolean("mc.show.steal", DEFAULT_SHOW_TYPE)
        set(value) = preferences.edit().putBoolean("mc.show.steal", value).apply()
    var showOther
        get() = preferences.getBoolean("mc.show.other", DEFAULT_SHOW_TYPE)
        set(value) = preferences.edit().putBoolean("mc.show.other", value).apply()
    var visibleDistance: Int
        get() {
            try {
                return preferences.getInt("mc.distance.show", DEFAULT_SHOW_DISTANCE)
            } catch (e: Exception) {
                return Integer.parseInt(preferences.getString("mc.distance.show", DEFAULT_SHOW_DISTANCE.toString()))
            }
        }
        set(value) {
            preferences.edit().putString("mc.distance.show", (if (value > Const.EQUATOR) Const.EQUATOR else value).toString()).apply()
        }
    var alarmDistance: Int
        get() {
            try {
                return preferences.getInt("mc.distance.alarm", DEFAULT_ALARM_DISTANCE)
            } catch (e: Exception) {
                return Integer.parseInt(preferences.getString("mc.distance.alarm", DEFAULT_ALARM_DISTANCE.toString()))
            }
        }
        set(value) {
            preferences.edit().putString("mc.distance.alarm", (if (value > Const.EQUATOR) Const.EQUATOR else value).toString()).apply()
        }
    var doNotDisturb
        get() = preferences.getBoolean("do.not.disturb", DEFAULT_DO_NOT_DISTURB)
        set(value) = preferences.edit().putBoolean("do.not.disturb", value).apply()
    var hoursAgo
        get() = Integer.parseInt(preferences.getString("hours.ago", DEFAULT_MAX_AGE.toString()))
        set(value) = preferences.edit().putString("hours.ago", value.toString()).apply()
    var maxNotifications
        get() = Integer.parseInt(preferences.getString("notifications.max", DEFAULT_MAX_NOTIFICATIONS.toString()))
        set(value) = preferences.edit().putString("notifications.max", value.toString()).apply()
    var onWay
        get() = preferences.getInt("mc.onway", 0)
        set(value) = preferences.edit().putInt("mc.onway", value).apply()
    var soundTitle: String
        get() = preferences.getString("mc.notification.sound.title", "default system")
        set(value) = preferences.edit().putString("mc.notification.sound.title", value).apply()
    var soundURI
        get() = preferences.getString("mc.notification.sound", "")
        set(uri) = preferences.edit().putString("mc.notification.sound", uri).apply()
    var sound: Uri? = null
        private set
    var vibration: Boolean
        get() = preferences.getBoolean("use.vibration", DEFAULT_VIBRATION)
        set(value) = preferences.edit().putBoolean("use.vibration", value).apply()
    var login
        get() = preferences.getString("mc.login", "")
        set(value) = preferences.edit().putString("mc.login", value).apply()
    var password
        get() = preferences.getString("mc.password", "")
        set(value) = preferences.edit().putString("mc.password", value).apply()
    var anonim
        get() = preferences.getBoolean("mc.anonim", DEFAULT_IS_ANONYMOUS)
        set(value) = preferences.edit().putBoolean("mc.anonim", value).apply()
    var appVersion
        get() = preferences.getInt("mc.app.version", 0)
        set(value) = preferences.edit().putInt("mc.app.version", value).apply()

    var savedLatLng
        get() = LatLng(preferences.getFloat("savedlat", DEFAULT_LATITUDE).toDouble(), preferences.getFloat("savedlng", DEFAULT_LONGITUDE).toDouble())
        set(latLng) = preferences.edit().putFloat("savedlat", latLng.latitude.toFloat()).putFloat("savedlng", latLng.longitude.toFloat()).apply()
    val GCMRegistrationCode = "mc.gcm.id"
    val GcmAppVersion = "gcm.app.version"

    init {
        initSound(context)
        try {
            val version = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            if (version != appVersion) {
                newVersion = true
                appVersion = version
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun setSound(title: String, uri: Uri) {
        soundTitle = title
        soundURI = uri.toString()
    }

    fun initSound(context: Context) {
        if (soundURI == "default") {
            sound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION)
        } else
            sound = Uri.parse(soundURI)
    }

    fun putBoolean(name: String, value: Boolean) {
        preferences.edit().putBoolean(name, value).apply()
    }


    fun setDefaultSoundAlarm() {
        soundTitle = "default system"
        soundURI = "default"
    }

    fun resetAuth() {
        preferences.edit().remove(login).remove(password).apply()
    }

    fun isHidden(type: Type): Boolean {
        when (type) {
            Type.BREAK                                               -> return !showBreak
            Type.MOTO_AUTO, Type.MOTO_MOTO, Type.MOTO_MAN, Type.SOLO -> return !showAcc
            Type.STEAL                                               -> return !showSteal
            Type.OTHER                                               -> return !showOther
            else                                                     -> return !showOther
        }
    }

    fun getPreferenceName(preference: String): String {
        when (preference) {
            "hoursAgo"         -> return "hours.ago"
            "showAcc"          -> return "mc.show.acc"
            "showBreak"        -> return "mc.show.break"
            "showSteal"        -> return "mc.show.steal"
            "showOther"        -> return "mc.show.other"
            "distanceShow"     -> return "mc.distance.show"
            "distanceAlarm"    -> return "mc.distance.alarm"
            "maxNotifications" -> return "notifications.max"
            "useVibration"     -> return "use.vibration"
            else               -> return "unknown"
        }
    }

    companion object {
        val DEFAULT_LATITUDE = 55.752295f
        val DEFAULT_LONGITUDE = 37.622735f
        private val DEFAULT_SHOW_DISTANCE = 200
        private val DEFAULT_ALARM_DISTANCE = 20
        private val DEFAULT_MAX_NOTIFICATIONS = 3
        private val DEFAULT_MAX_AGE = 24
        private val DEFAULT_VIBRATION = true
        private val DEFAULT_DO_NOT_DISTURB = false
        private val DEFAULT_IS_ANONYMOUS = false
        private val DEFAULT_SHOW_TYPE = true

        fun getInstance(context: Context): Preferences {
            if (Holder.instance == null) {
                Holder.instance = Preferences(context)
                (Holder.instance as Preferences).doNotDisturb = false
            }
            return Holder.instance as Preferences
        }

        fun dirtyRead(): Preferences? {
            return Holder.instance
        }

    }

    private object Holder {
        var instance: Preferences? = null
    }
}
