package motocitizen.utils

import android.preference.Preference
import android.preference.PreferenceFragment
import motocitizen.datasources.preferences.Preferences

fun PreferenceFragment.preferenceByName(name: String): Preference = findPreference(Preferences.getPreferenceName(name))