package motocitizen.ui.fragments

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import motocitizen.content.AccidentsController
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.notifications.Messaging
import motocitizen.ui.Screens
import motocitizen.user.Auth
import motocitizen.user.User
import motocitizen.utils.EQUATOR
import motocitizen.utils.goTo
import motocitizen.utils.showToast

class SettingsFragment : PreferenceFragmentCompat() {
    private val PREFERENCES = R.xml.preferences

    // Инициализация будет происходить после onCreatePreferences
    private lateinit var buttonAuth: Preference
    private lateinit var buttonSound: Preference
    private lateinit var notificationSoundPreference: Preference
    private lateinit var authPreference: Preference

    private lateinit var notificationDistPreference: Preference
    private lateinit var notificationAlarmPreference: Preference
    private lateinit var showAcc: CheckBoxPreference
    private lateinit var showBreak: CheckBoxPreference
    private lateinit var showSteal: CheckBoxPreference
    private lateinit var showOther: CheckBoxPreference
    private lateinit var hoursAgo: Preference
    private lateinit var isTester: CheckBoxPreference
    private lateinit var maxNotifications: Preference
    private lateinit var useVibration: Preference

    private var login = Preferences.login

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(PREFERENCES, rootKey)

        // Привязка всех Preferences после инициализации
        buttonAuth = requirePreference(R.string.settings_auth_button)
        buttonSound = requirePreference(R.string.notification_sound)
        notificationSoundPreference = requirePreference(R.string.notification_sound)
        authPreference = requirePreference(R.string.settings_auth_button)

        notificationDistPreference = requirePreference("distanceShow")
        notificationAlarmPreference = requirePreference("distanceAlarm")
        showAcc = requireCheckBox("showAcc")
        showBreak = requireCheckBox("showBreak")
        showSteal = requireCheckBox("showSteal")
        showOther = requireCheckBox("showOther")
        hoursAgo = requirePreference("hoursAgo")
        isTester = requireCheckBox("isTester")
        maxNotifications = requirePreference("maxNotifications")
        useVibration = requirePreference("useVibration")

        setUpListeners()
        update()
    }

    private fun <T : Preference> requirePreference(key: String): T =
        findPreference(Preferences.getPreferenceName(key)) ?: error("Preference not found: $key")

    private fun <T : Preference> requirePreference(resId: Int): T =
        findPreference(getString(resId)) ?: error("Preference not found: ${getString(resId)}")

    private fun requireCheckBox(key: String): CheckBoxPreference =
        requirePreference(key)

    private fun Preference.onChangeListener(callback: (Preference, Any) -> Boolean) {
        onPreferenceChangeListener = Preference.OnPreferenceChangeListener(callback)
    }

    private fun Preference.onClickListener(callback: (Preference) -> Boolean) {
        onPreferenceClickListener = Preference.OnPreferenceClickListener(callback)
    }

    private fun update() {
        authPreference.summary = if (login.isNotEmpty()) "${User.roleName}: $login" else User.roleName
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

        arrayOf(showAcc, showBreak, showOther, showSteal).forEach {
            it.onChangeListener(::visibleListener)
        }
    }

    private fun maxNotificationsListener(preference: Preference, newValue: Any): Boolean {
        preference.summary = newValue.toString()
        return true
    }

    private fun visibleListener(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            "mc.show.acc" -> Preferences.showAccidents = newValue as Boolean
            "mc.show.break" -> Preferences.showBreaks = newValue as Boolean
            "mc.show.steal" -> Preferences.showSteal = newValue as Boolean
            "mc.show.other" -> Preferences.showOther = newValue as Boolean
        }
        if (isAllHidden) activity?.showToast(getString(R.string.no_one_accident_visible))
        update()
        return false
    }

    private fun isTesterListener(preference: Preference, newValue: Any): Boolean {
        Preferences.isTester = newValue as Boolean
        if (newValue) {
            Messaging.subscribeToTest()
        } else {
            Messaging.unSubscribeFromTest()
        }
        AccidentsController.resetLastUpdate()
        update()
        return false
    }

    private fun hoursAgoListener(preference: Preference, newValue: Any): Boolean {
        val value = if (newValue == "0") "1" else newValue.toString()
        preference.summary = value
        AccidentsController.resetLastUpdate()
        return true
    }

    private fun vibrationListener(newValue: Any): Boolean {
        Preferences.vibration = newValue as Boolean
        return true
    }

    private fun authButtonPressed(): Boolean {
        Auth.logout()
        requireActivity().goTo(Screens.AUTH)
        return true
    }

    private fun soundButtonPressed(): Boolean {
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, SelectSoundFragment())
            .addToBackStack(null)
            .commit()
        return true
    }

    private fun distanceListener(preference: Preference, newValue: Any): Boolean {
        val value = (newValue as String).toInt().coerceAtMost(EQUATOR)
        when (preference) {
            notificationDistPreference -> Preferences.visibleDistance = value
            notificationAlarmPreference -> Preferences.alarmDistance = value
        }
        update()
        return false
    }

    private val isAllHidden: Boolean
        get() = !with(Preferences) { showAccidents || showSteal || showBreaks || showOther }
}
