package motocitizen.datasources.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager

import com.google.android.gms.maps.model.LatLng
import motocitizen.datasources.preferences.Preferences.Stored.*

import motocitizen.dictionary.Type
import motocitizen.utils.EQUATOR

object Preferences {
    private val DEFAULT_LATITUDE = 55.752295f
    private val DEFAULT_LONGITUDE = 37.622735f
    private val DEFAULT_SHOW_DISTANCE = 200
    private val DEFAULT_ALARM_DISTANCE = 20
    private val DEFAULT_MAX_NOTIFICATIONS = 3
    private val DEFAULT_MAX_AGE = 24
    private val DEFAULT_VIBRATION = true
    private val DEFAULT_DO_NOT_DISTURB = false
    private val DEFAULT_IS_ANONYMOUS = false
    private val DEFAULT_SHOW_TYPE = true

    enum class Stored(val key: String, val default: Any) {
        IS_SHOW_ACCIDENT("mc.show.acc", DEFAULT_SHOW_TYPE),
        IS_SHOW_BREAK("mc.show.break", DEFAULT_SHOW_TYPE),
        IS_SHOW_STEAL("mc.show.steal", DEFAULT_SHOW_TYPE),
        IS_SHOW_OTHER("mc.show.other", DEFAULT_SHOW_TYPE),
        VISIBLE_DISTANCE("mc.distance.show", DEFAULT_SHOW_DISTANCE),
        ALARM_DISTANCE("mc.distance.alarm", DEFAULT_ALARM_DISTANCE),
        DO_NOT_DISTURB("do.not.disturb", DEFAULT_DO_NOT_DISTURB),
        HOURS_AGO("hours.ago", DEFAULT_MAX_AGE),
        MAX_NOTIFICATIONS("notifications.max", DEFAULT_MAX_NOTIFICATIONS),
        VIBRATION("use.vibration", DEFAULT_VIBRATION),
        ANONYMOUS("mc.anonim", DEFAULT_IS_ANONYMOUS),
        LATITUDE("savedlat", DEFAULT_LATITUDE),
        LONGITUDE("savedlng", DEFAULT_LONGITUDE),
        ON_WAY("mc.onway", 0),
        APP_VERSION("mc.app.version", 0),
        SOUND_TITLE("mc.notification.sound.title", "default system"),
        SOUND_URI("mc.notification.sound", ""),
        LOGIN("mc.login", ""),
        PASSWORD("mc.password", "")
    }

    fun initialize(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
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


    var newVersion = false
    lateinit private var preferences: SharedPreferences

    var showAcc
        get() = getBoolean(IS_SHOW_ACCIDENT)
        set(value) = putBoolean(IS_SHOW_ACCIDENT, value)
    var showBreak
        get() = getBoolean(IS_SHOW_BREAK)
        set(value) = putBoolean(IS_SHOW_BREAK, value)
    var showSteal
        get() = getBoolean(IS_SHOW_STEAL)
        set(value) = putBoolean(IS_SHOW_STEAL, value)
    var showOther
        get() = getBoolean(IS_SHOW_OTHER)
        set(value) = putBoolean(IS_SHOW_OTHER, value)
    //todo разобраться
    var visibleDistance: Int
        get() {
            return try {
                getInt(VISIBLE_DISTANCE)
            } catch (e: Exception) {
                Integer.parseInt(getString(VISIBLE_DISTANCE))
            }
        }
        set(value) {
            putString(VISIBLE_DISTANCE, if (value > EQUATOR) EQUATOR else value)
        }
    var alarmDistance: Int
        get() {
            return try {
                getInt(ALARM_DISTANCE)
            } catch (e: Exception) {
                Integer.parseInt(getString(ALARM_DISTANCE))
            }
        }
        set(value) {
            putString(ALARM_DISTANCE, if (value > EQUATOR) EQUATOR else value)
        }
    var doNotDisturb
        get() = getBoolean(DO_NOT_DISTURB)
        set(value) = putBoolean(DO_NOT_DISTURB, value)
    var hoursAgo
        get() = Integer.parseInt(getString(HOURS_AGO))
        set(value) = putString(HOURS_AGO, value)
    var maxNotifications
        get() = Integer.parseInt(getString(MAX_NOTIFICATIONS))
        set(value) = putString(MAX_NOTIFICATIONS, value)
    var onWay
        get() = getInt(ON_WAY)
        set(value) = putInt(ON_WAY, value)
    var soundTitle: String
        get() = getString(SOUND_TITLE)
        set(value) = putString(SOUND_TITLE, value)
    var soundURI: String
        get() = getString(SOUND_URI)
        set(uri) = putString(SOUND_URI, uri)
    var sound: Uri? = null
        private set
    var vibration: Boolean
        get() = getBoolean(VIBRATION)
        set(value) = putBoolean(VIBRATION, value)
    var login: String
        get() = getString(LOGIN)
        set(value) = putString(LOGIN, value)
    var password: String
        get() = getString(PASSWORD)
        set(value) = putString(PASSWORD, value)
    var anonymous
        get() = getBoolean(ANONYMOUS)
        set(value) = putBoolean(ANONYMOUS, value)
    var appVersion
        get() = getInt(APP_VERSION)
        set(value) = putInt(APP_VERSION, value)

    var savedLatLng
        get() = LatLng(getFloat(LATITUDE).toDouble(), getFloat(LONGITUDE).toDouble())
        set(latLng) {
            putFloat(LATITUDE, latLng.latitude.toFloat())
            putFloat(LONGITUDE, latLng.longitude.toFloat())
        }

    fun setSound(title: String, uri: Uri) {
        soundTitle = title
        soundURI = uri.toString()
    }

    fun initSound(context: Context) {
        sound = if (soundURI == "default") {
            RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION)
        } else
            Uri.parse(soundURI)
    }

    fun setDefaultSoundAlarm() {
        soundTitle = "default system"
        soundURI = "default"
    }

    fun resetAuth() {
        preferences.edit().remove(login).remove(password).apply()
    }

    fun isEnabled(type: Type): Boolean = when (type) {
        Type.BREAK                                               -> showBreak
        Type.MOTO_AUTO, Type.MOTO_MOTO, Type.MOTO_MAN, Type.SOLO -> showAcc
        Type.STEAL                                               -> showSteal
        Type.OTHER                                               -> showOther
        else                                                     -> true
    }

    //todo wtf!?
    fun getPreferenceName(preference: String): String = when (preference) {
        "hoursAgo"         -> "hours.ago"
        "showAcc"          -> "mc.show.acc"
        "showBreak"        -> "mc.show.break"
        "showSteal"        -> "mc.show.steal"
        "showOther"        -> "mc.show.other"
        "distanceShow"     -> "mc.distance.show"
        "distanceAlarm"    -> "mc.distance.alarm"
        "maxNotifications" -> "notifications.max"
        "useVibration"     -> "use.vibration"
        else               -> "unknown"
    }

    private fun putString(stored: Stored, value: Any) = preferences.edit().putString(stored.key, value.toString()).apply()
    private fun getString(stored: Stored) = preferences.getString(stored.key, stored.default.toString())

    private fun putBoolean(stored: Stored, value: Boolean) = preferences.edit().putBoolean(stored.key, value).apply()
    private fun getBoolean(stored: Stored) = preferences.getBoolean(stored.key, stored.default as Boolean)

    private fun putInt(stored: Stored, value: Int) = preferences.edit().putInt(stored.key, value).apply()
    private fun getInt(stored: Stored) = preferences.getInt(stored.key, stored.default as Int)

    private fun putFloat(stored: Stored, value: Float) = preferences.edit().putFloat(stored.key, value).apply()
    private fun getFloat(stored: Stored) = preferences.getFloat(stored.key, stored.default as Float)
}

