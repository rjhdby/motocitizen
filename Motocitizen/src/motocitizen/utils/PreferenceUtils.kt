package motocitizen.utils

import android.preference.Preference
import android.preference.PreferenceFragment
import motocitizen.datasources.preferences.Preferences

fun PreferenceFragment.preferenceByName(name: String): Preference = findPreference(Preferences.getPreferenceName(name))
fun Preference.onChangeListener(callback: (Preference, Any) -> Boolean) {
    onPreferenceChangeListener = Preference.OnPreferenceChangeListener(callback)
}

fun Preference.onClickListener(callback: (Preference) -> Boolean) {
    onPreferenceClickListener = Preference.OnPreferenceClickListener(callback)
}