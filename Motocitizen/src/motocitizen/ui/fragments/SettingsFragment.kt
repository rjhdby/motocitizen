package motocitizen.ui.fragments

import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import com.google.firebase.messaging.FirebaseMessaging
import motocitizen.content.AccidentsController
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.user.Auth
import motocitizen.user.User
import motocitizen.utils.*

class SettingsFragment : PreferenceFragment() {
    private val PREFERENCES = R.xml.preferences

    private val notificationDistPreference: Preference by lazy { preferenceByName("distanceShow") }
    private val notificationAlarmPreference: Preference by lazy { preferenceByName("distanceAlarm") }
    private val buttonAuth: Preference by lazy { findPreference(resources.getString(R.string.settings_auth_button)) }
    private val buttonSound: Preference by lazy { findPreference(resources.getString(R.string.notification_sound)) }
    private val showAcc: Preference by lazy { preferenceByName("showAcc") }
    private val showBreak: Preference by lazy { preferenceByName("showBreak") }
    private val showSteal: Preference by lazy { preferenceByName("showSteal") }
    private val showOther: Preference by lazy { preferenceByName("showOther") }
    private val hoursAgo: Preference by lazy { preferenceByName("hoursAgo") }
    private val isTester: Preference by lazy { preferenceByName("isTester") }
    private val maxNotifications: Preference by lazy { preferenceByName("maxNotifications") }
    private val useVibration: Preference by lazy { preferenceByName("useVibration") }
    private val notificationSoundPreference: Preference by lazy { findPreference(resources.getString(R.string.notification_sound)) }
    private val authPreference: Preference by lazy { findPreference(resources.getString(R.string.settings_auth_button)) }

    private var login = Preferences.login

    override fun onResume() {
        super.onResume()
        preferenceScreen = null
        addPreferencesFromResource(PREFERENCES)
        setUpListeners()
        update()
    }

    private fun update() {
        authPreference.summary = if (login.isNotEmpty()) User.roleName + ": " + login else User.roleName
        maxNotifications.summary = Preferences.maxNotifications.toString()
        hoursAgo.summary = Preferences.hoursAgo.toString()
        notificationSoundPreference.summary = Preferences.soundTitle
        notificationDistPreference.summary = Preferences.visibleDistance.toString()
        notificationAlarmPreference.summary = Preferences.alarmDistance.toString()
        (showAcc as CheckBoxPreference).isChecked = Preferences.showAccidents
        (showBreak as CheckBoxPreference).isChecked = Preferences.showBreaks
        (showSteal as CheckBoxPreference).isChecked = Preferences.showSteal
        (showOther as CheckBoxPreference).isChecked = Preferences.showOther
        (isTester as CheckBoxPreference).isChecked = Preferences.isTester
    }

    private fun setUpListeners() {
        buttonSound.onClickListener { this.soundButtonPressed() }
        buttonAuth.onClickListener { this.authButtonPressed() }

        notificationDistPreference.onChangeListener(::distanceListener)
        notificationAlarmPreference.onChangeListener(::distanceListener)
        maxNotifications.onChangeListener(::maxNotificationsListener)
        hoursAgo.onChangeListener(::hoursAgoListener)
        useVibration.onChangeListener { _, newValue -> this.vibrationListener(newValue) }
        isTester.onChangeListener(::isTesterListener)
        arrayOf(showAcc, showBreak, showOther, showSteal)
                .forEach { it.onChangeListener(::visibleListener) }
    }

    private fun maxNotificationsListener(preference: Preference, newValue: Any): Boolean {
        preference.summary = newValue as String
        return true
    }

    private fun visibleListener(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            "mc.show.acc"   -> Preferences.showAccidents = newValue as Boolean
            "mc.show.break" -> Preferences.showBreaks = newValue as Boolean
            "mc.show.steal" -> Preferences.showSteal = newValue as Boolean
            "mc.show.other" -> Preferences.showOther = newValue as Boolean
        }
        if (isAllHidden) {
            activity.showToast(getString(R.string.no_one_accident_visible))
        }
        update()
        return false
    }

    private fun isTesterListener(preference: Preference, newValue: Any): Boolean {
        Preferences.isTester = newValue as Boolean
        if (newValue) {
            FirebaseMessaging.getInstance().subscribeToTopic("test")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("test")
        }
        AccidentsController.resetLastUpdate()
        update()
        return false
    }

    private fun hoursAgoListener(preference: Preference, newValue: Any): Boolean {
        var value = newValue
        if (value == "0") value = "1"
        preference.summary = value.toString()
        AccidentsController.resetLastUpdate()
        return true
    }

    private fun vibrationListener(newValue: Any): Boolean {
        Preferences.vibration = newValue as Boolean
        return true
    }

    private fun authButtonPressed(): Boolean {
        Auth.logoff()
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
            notificationDistPreference  -> Preferences.visibleDistance = value
            notificationAlarmPreference -> Preferences.alarmDistance = value
        }
        update()
        return false
    }


    private val isAllHidden: Boolean
        inline get() = !with(Preferences) { showAccidents || showSteal || showBreaks || showOther }
}
