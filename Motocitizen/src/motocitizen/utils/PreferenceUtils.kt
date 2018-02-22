package motocitizen.utils

import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import motocitizen.datasources.preferences.Preferences

fun Preference.onChangeListener(callback: (Preference, Any) -> Boolean) {
    onPreferenceChangeListener = Preference.OnPreferenceChangeListener(callback)
}

fun Preference.onClickListener(callback: (Preference) -> Boolean) {
    onPreferenceClickListener = Preference.OnPreferenceClickListener(callback)
}

@Suppress("UNCHECKED_CAST")
private fun <T : Preference> PreferenceFragment.preferenceBinder(name: String) = lazy { findPreference(Preferences.getPreferenceName(name)) as T }

fun PreferenceFragment.bindPreference(name: String) = preferenceBinder<Preference>(name)

fun PreferenceFragment.bindCheckBoxPreference(name: String) = preferenceBinder<CheckBoxPreference>(name)