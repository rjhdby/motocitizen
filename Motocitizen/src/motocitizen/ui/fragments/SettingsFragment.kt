package motocitizen.ui.fragments

import android.preference.Preference
import android.preference.PreferenceFragment
import motocitizen.datasources.preferences.Preferences
import motocitizen.datasources.preferences.Preferences.Stored.*
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.user.User
import motocitizen.utils.*

class SettingsFragment : PreferenceFragment() {
    private val PREFERENCES = R.xml.preferences

    private val preferences = Preferences

    private val notificationDistPreference: Preference by lazy { preferenceByName("distanceShow") }
    private val notificationAlarmPreference: Preference by lazy { preferenceByName("distanceAlarm") }
    private val buttonAuth: Preference by lazy { findPreference(resources.getString(R.string.settings_auth_button)) }
    private val buttonSound: Preference by lazy { findPreference(resources.getString(R.string.notification_sound)) }
    private val showAcc: Preference by lazy { preferenceByName("showAcc") }
    private val showBreak: Preference by lazy { preferenceByName("showBreak") }
    private val showSteal: Preference by lazy { preferenceByName("showSteal") }
    private val showOther: Preference by lazy { preferenceByName("showOther") }
    private val hoursAgo: Preference by lazy { preferenceByName("hoursAgo") }
    private val maxNotifications: Preference by lazy { preferenceByName("maxNotifications") }
    private val useVibration: Preference by lazy { preferenceByName("useVibration") }
    private val notificationSoundPreference: Preference by lazy { findPreference(resources.getString(R.string.notification_sound)) }
    private val authPreference: Preference by lazy { findPreference(resources.getString(R.string.settings_auth_button)) }

    private var login = preferences.login

    override fun onResume() {
        super.onResume()
        preferenceScreen = null
        addPreferencesFromResource(PREFERENCES)
        setUpListeners()
        update()
    }

    private fun update() {
        authPreference.summary = if (login.isNotEmpty()) User.roleName + ": " + login else User.roleName
        maxNotifications.summary = MAX_NOTIFICATIONS.string()
        hoursAgo.summary = HOURS_AGO.string()
        notificationSoundPreference.summary = preferences.soundTitle
        notificationDistPreference.summary = VISIBLE_DISTANCE.string()
        notificationAlarmPreference.summary = ALARM_DISTANCE.string()
    }

    private fun setUpListeners() {
        buttonSound.onClickListener { this.soundButtonPressed() }
        buttonAuth.onClickListener { this.authButtonPressed() }

        notificationDistPreference.onChangeListener(this::distanceListener)
        notificationAlarmPreference.onChangeListener(this::distanceListener)
        maxNotifications.onChangeListener(this::maxNotificationsListener)
        hoursAgo.onChangeListener(this::hoursAgoListener)
        useVibration.onChangeListener { _, newValue -> this.vibrationListener(newValue) }
        showAcc.onChangeListener(this::visibleListener)
        showBreak.onChangeListener(this::visibleListener)
        showSteal.onChangeListener(this::visibleListener)
        showOther.onChangeListener(this::visibleListener)
    }

    private fun maxNotificationsListener(preference: Preference, newValue: Any): Boolean {
        preference.summary = newValue as String
        return true
    }

    private fun hoursAgoListener(preference: Preference, newValue: Any): Boolean {
        var value = newValue
        if (value == "0") value = "1"
        preference.summary = value.toString()
        return true
    }

    private fun vibrationListener(newValue: Any): Boolean {
        VIBRATION.put(newValue)
        return true
    }

    private fun authButtonPressed(): Boolean {
        Router.goTo(activity, Router.Target.AUTH)
        return true
    }

    private fun soundButtonPressed(): Boolean {
        fragmentManager.beginTransaction().replace(android.R.id.content, SelectSoundFragment()).commit()
        return true
    }

    private fun distanceListener(preference: Preference, newValue: Any): Boolean {
        val value = Math.min(EQUATOR, Integer.parseInt(newValue as String))

        when (preference) {
            notificationDistPreference  -> VISIBLE_DISTANCE.put(value)
            notificationAlarmPreference -> ALARM_DISTANCE.put(value)
        }
        update()
        return false
    }

    private fun visibleListener(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            "mc.show.acc"   -> IS_SHOW_ACCIDENT.put(newValue)
            "mc.show.break" -> IS_SHOW_BREAK.put(newValue)
            "mc.show.steal" -> IS_SHOW_STEAL.put(newValue)
            "mc.show.other" -> IS_SHOW_OTHER.put(newValue)
        }
        if (isAllHidden) {
            show(activity, getString(R.string.no_one_accident_visible))
        }
        update()
        return false
    }

    private val isAllHidden: Boolean
        inline get() = !(IS_SHOW_ACCIDENT.boolean() || IS_SHOW_BREAK.boolean() || IS_SHOW_STEAL.boolean() || IS_SHOW_OTHER.boolean())
}
