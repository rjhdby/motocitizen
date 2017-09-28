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
        PASSWORD("mc.password", "");

        fun put(value: Any) {
            val editor = preferences.edit()
            when (default) {
                is Int     -> editor.putInt(key, value as Int)
                is Boolean -> editor.putBoolean(key, value as Boolean)
                is String  -> editor.putString(key, value as String)
                is Float   -> editor.putFloat(key, value as Float)
            }
            editor.apply()
        }

        fun boolean(): Boolean = preferences.getBoolean(key, default as Boolean)
        fun string(): String = preferences.getString(key, default.toString())
        fun float(): Float = preferences.getFloat(key, default as Float)
        fun int(): Int = preferences.getInt(key, default as Int)

//        private fun get(key:String):Any{
//            preferences.
//        }
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

    var doNotDisturb
        get() = getBoolean(DO_NOT_DISTURB)
        set(value) = DO_NOT_DISTURB.put(value)
    var onWay
        get() = getInt(ON_WAY)
        set(value) = ON_WAY.put(value)
    var soundTitle: String
        get() = getString(SOUND_TITLE)
        set(value) = SOUND_TITLE.put(value)
    var soundURI: String
        get() = getString(SOUND_URI)
        set(uri) = SOUND_URI.put(uri)
    var sound: Uri? = null
        private set
    var login: String
        get() = LOGIN.string()
        set(value) = LOGIN.put(value)
    var password: String
        get() = PASSWORD.string()
        set(value) = PASSWORD.put(value)
    var anonymous
        get() = getBoolean(ANONYMOUS)
        set(value) = ANONYMOUS.put(value)
    var appVersion
        get() = getInt(APP_VERSION)
        set(value) = APP_VERSION.put(value)

    var savedLatLng
        get() = LatLng(getFloat(LATITUDE).toDouble(), getFloat(LONGITUDE).toDouble())
        set(latLng) {
            LATITUDE.put(latLng.latitude.toFloat())
            LONGITUDE.put(latLng.longitude.toFloat())
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
        preferences.edit().remove(LOGIN.key).remove(PASSWORD.key).apply()
    }

    fun isEnabled(type: Type): Boolean = when (type) {
        Type.BREAK                                               -> IS_SHOW_BREAK.boolean()
        Type.MOTO_AUTO, Type.MOTO_MOTO, Type.MOTO_MAN, Type.SOLO -> IS_SHOW_ACCIDENT.boolean()
        Type.STEAL                                               -> IS_SHOW_STEAL.boolean()
        Type.OTHER                                               -> IS_SHOW_OTHER.boolean()
        Type.USER                                                -> true
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

    private fun getString(stored: Stored) = preferences.getString(stored.key, stored.default.toString())

    private fun getBoolean(stored: Stored) = preferences.getBoolean(stored.key, stored.default as Boolean)

    private fun getInt(stored: Stored) = preferences.getInt(stored.key, stored.default as Int)

    private fun getFloat(stored: Stored) = preferences.getFloat(stored.key, stored.default as Float)
}

