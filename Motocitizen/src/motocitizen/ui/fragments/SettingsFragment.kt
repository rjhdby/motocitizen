package motocitizen.ui.fragments

import android.preference.Preference
import android.preference.PreferenceFragment
import motocitizen.content.AccidentsController
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.notifications.Messaging
import motocitizen.ui.Screens
import motocitizen.user.Auth
import motocitizen.user.User
import motocitizen.utils.*

class SettingsFragment : PreferenceFragment() {
    companion object {
        private const val PREFERENCES = R.xml.preferences
    }

    private val buttonAuth: Preference by lazy { findPreference(resources.getString(R.string.settings_auth_button)) }
    private val buttonSound: Preference by lazy { findPreference(resources.getString(R.string.notification_sound)) }
    private val notificationSoundPreference: Preference by lazy { findPreference(resources.getString(R.string.notification_sound)) }
    private val authPreference: Preference by lazy { findPreference(resources.getString(R.string.settings_auth_button)) }

    private val notificationDistPreference by bindPreference("distanceShow")
    private val notificationAlarmPreference by bindPreference("distanceAlarm")
    private val showAcc by bindCheckBoxPreference("showAcc")
    private val showBreak by bindCheckBoxPreference("showBreak")
    private val showSteal by bindCheckBoxPreference("showSteal")
    private val showOther by bindCheckBoxPreference("showOther")
    private val hoursAgo by bindPreference("hoursAgo")
    private val isTester by bindCheckBoxPreference("isTester")
    private val maxNotifications by bindPreference("maxNotifications")
    private val useVibration by bindPreference("useVibration")

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
        showAcc.isChecked = Preferences.showAccidents
        showBreak.isChecked = Preferences.showBreaks
        showSteal.isChecked = Preferences.showSteal
        showOther.isChecked = Preferences.showOther
        isTester.isChecked = Preferences.isTester
    }

    private fun setUpListeners() {
        buttonSound.onClickListener { soundButtonPressed() }
        buttonAuth.onClickListener { authButtonPressed() }

        notificationDistPreference.onChangeListener(::distanceListener)
        notificationAlarmPreference.onChangeListener(::distanceListener)
        maxNotifications.onChangeListener(::maxNotificationsListener)
        hoursAgo.onChangeListener(::hoursAgoListener)
        useVibration.onChangeListener { _, newValue -> vibrationListener(newValue) }
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
        Messaging.apply {
            if (newValue) subscribeToTest() else unSubscribeFromTest()
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
        Auth.logout()
        goTo(Screens.AUTH)
        return true
    }

    private fun soundButtonPressed(): Boolean {
        fragmentManager.beginTransaction().replace(android.R.id.content, SelectSoundFragment()).commit()
        return true
    }

    private fun distanceListener(preference: Preference, newValue: Any): Boolean {
        val value = (newValue as String).toInt().coerceAtMost(EQUATOR)

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
