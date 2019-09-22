package motocitizen.datasources.preferences

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import motocitizen.MyApp
import motocitizen.datasources.preferences.Preferences.Stored.*
import motocitizen.dictionary.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Preferences {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApp.context)

    var appVersion by PreferenceDelegate<Int>(APP_VERSION)
    var doNotDisturb by PreferenceDelegate<Boolean>(DO_NOT_DISTURB)
    var onWay by PreferenceDelegate<Int>(ON_WAY)
    var soundTitle by PreferenceDelegate<String>(SOUND_TITLE)
    var soundURI by PreferenceDelegate<String>(SOUND_URI)
    lateinit var sound: Uri
        private set
    var login by PreferenceDelegate<String>(LOGIN)
    var password by PreferenceDelegate<String>(PASSWORD)

    var visibleDistance by PreferenceDelegate<Int>(VISIBLE_DISTANCE)
    var alarmDistance by PreferenceDelegate<Int>(ALARM_DISTANCE)

    var showAccidents by PreferenceDelegate<Boolean>(IS_SHOW_ACCIDENT)
    var showBreaks by PreferenceDelegate<Boolean>(IS_SHOW_BREAK)
    var showSteal by PreferenceDelegate<Boolean>(IS_SHOW_STEAL)
    var showOther by PreferenceDelegate<Boolean>(IS_SHOW_OTHER)

    var vibration by PreferenceDelegate<Boolean>(VIBRATION)

    var hoursAgo by PreferenceDelegate<Int>(HOURS_AGO)

    var maxNotifications by PreferenceDelegate<Int>(MAX_NOTIFICATIONS)

    var isTester by PreferenceDelegate<Boolean>(IS_TESTER)

    private var latitude by PreferenceDelegate<Float>(LATITUDE)
    private var longitude by PreferenceDelegate<Float>(LONGITUDE)

    var savedLatLng
        get() = LatLng(latitude.toDouble(), longitude.toDouble())
        set(latLng) {
            latitude = latLng.latitude.toFloat()
            longitude = latLng.longitude.toFloat()
        }

    var authType by PreferenceDelegate<String>(AUTH_TYPE)

    var vkToken by PreferenceDelegate<String>(VK_TOKEN)

    var googleAccount by PreferenceDelegate<String>(GOOGLE_ACCOUNT)

    var googleName by PreferenceDelegate<String>(GOOGLE_NAME)

    init {
        initSound(MyApp.context)
        val oldVersion = appVersion
        appVersion = MyApp.context.packageManager.getPackageInfo(MyApp.context.packageName, 0).versionCode
        if (oldVersion != appVersion) MyApp.firstStart = true
    }

    private const val DEFAULT_LATITUDE = 55.752295f
    private const val DEFAULT_LONGITUDE = 37.622735f
    private const val DEFAULT_SHOW_DISTANCE = 200
    private const val DEFAULT_ALARM_DISTANCE = 20
    private const val DEFAULT_MAX_NOTIFICATIONS = 3
    private const val DEFAULT_MAX_AGE = 24
    private const val DEFAULT_VIBRATION = true
    private const val DEFAULT_DO_NOT_DISTURB = false
    private const val DEFAULT_SHOW_TYPE = true
    private const val DEFAULT_TESTER = false
    private const val DEFAULT_AUTH_TYPE = "none"

    private enum class Stored(val key: String, val default: Any) {
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
        LATITUDE("savedlat", DEFAULT_LATITUDE),
        LONGITUDE("savedlng", DEFAULT_LONGITUDE),
        ON_WAY("mc.onway", 0),
        APP_VERSION("mc.app.version", 0),
        SOUND_TITLE("mc.notification.sound.title", "default system"),
        SOUND_URI("mc.notification.sound", ""),
        LOGIN("mc.login", ""),
        PASSWORD("mc.password", ""),
        IS_TESTER("tester", DEFAULT_TESTER),
        AUTH_TYPE("authType", DEFAULT_AUTH_TYPE),
        VK_TOKEN("vkToken", ""),
        GOOGLE_ACCOUNT("googleAccount", ""),
        GOOGLE_NAME("googleName", "");
    }

    init {
        values()
                .filterNot { preferences.contains(it.key) }
                .forEach { setValue(it, it.default) }
    }

    //todo refactor sound settings
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

    fun isEnabled(type: Type): Boolean = when (type) {
        Type.BREAK                                               -> showBreaks
        Type.MOTO_AUTO, Type.MOTO_MOTO, Type.MOTO_MAN, Type.SOLO -> showAccidents
        Type.STEAL                                               -> showSteal
        Type.OTHER                                               -> showOther
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
        "isTester"         -> "tester"
        else               -> "unknown"
    }

    private fun setValue(stored: Stored, value: Any) {
        preferences.edit().apply {
            when (stored.default) {
                is Int     -> putInt(stored.key, value as Int)
                is Boolean -> putBoolean(stored.key, value as Boolean)
                is String  -> putString(stored.key, value as String)
                is Float   -> putFloat(stored.key, value as Float)
            }
        }.apply()
    }

    private class PreferenceDelegate<T>(private val stored: Stored) : ReadWriteProperty<Preferences, T> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Preferences, property: KProperty<*>): T =
                when (stored.default) {
                    is String  -> preferences.getString(stored.key, stored.default) as T
                    is Boolean -> preferences.getBoolean(stored.key, stored.default) as T
                    is Int     -> preferences.getInt(stored.key, stored.default) as T
                    is Float   -> preferences.getFloat(stored.key, stored.default) as T
                    else       -> throw TypeCastException("Wrong property type")
                }

        override fun setValue(thisRef: Preferences, property: KProperty<*>, value: T) {
            setValue(stored, value as Any)
        }
    }
}