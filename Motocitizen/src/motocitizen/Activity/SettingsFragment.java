package motocitizen.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Configuration;
import motocitizen.utils.Const;

public class SettingsFragment extends PreferenceFragment{

    private SharedPreferences prefs;
    private ListPreference mapProviderPreference;
    private Preference nottifDistPreference, nottifAlarmPreference;
    private Preference authPreference,nottifSoundPreference;
    private Preference showAccidents, showSteals, showBreaks, showOther;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        prefs = PreferenceManager.getDefaultSharedPreferences(Startup.context);
        Preference buttonAuth = (Preference)findPreference(getResources().getString(R.string.mc_settings_auth_button));
        buttonAuth.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(Startup.context, AuthActivity.class);
                Startup.context.startActivity(i);
                return true;
            }
        });
        Preference buttonSound = (Preference)findPreference(getResources().getString(R.string.mc_notif_sound));
        buttonSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(Startup.context, SelectSoundActivity.class);
                Startup.context.startActivity(i);
                return true;
            }
        });
        nottifDistPreference = findPreference(Configuration.MC_DISTANCE_SHOW);
        nottifAlarmPreference = findPreference(Configuration.MC_DISTANCE_ALARM);
        nottifSoundPreference = findPreference(getResources().getString(R.string.mc_notif_sound));
        authPreference = findPreference(getResources().getString(R.string.mc_settings_auth_button));
        showAccidents = findPreference(Configuration.MC_SHOW_ACC);
        showSteals = findPreference(Configuration.MC_SHOW_STEAL);
        showBreaks = findPreference(Configuration.MC_SHOW_BREAK);
        showOther = findPreference(Configuration.MC_SHOW_OTHER);

        mapProviderPreference = (ListPreference)getPreferenceScreen().findPreference("map_pref");
        mapProviderPreference.setOnPreferenceChangeListener(mapProviderListener);
        nottifDistPreference.setOnPreferenceChangeListener(distanceListener);
        nottifAlarmPreference.setOnPreferenceChangeListener(distanceListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO Сделать однообразно
        authPreference.setSummary(prefs.getString("mc.login", ""));
        nottifSoundPreference.setSummary(prefs.getString("mc.notification.sound.title", getString(R.string.mc_notif_system)));
        nottifDistPreference.setSummary(prefs.getString("mc.distance.show", "200"));
        nottifAlarmPreference.setSummary(prefs.getString("mc.distance.alarm", "20"));
    }

    Preference.OnPreferenceChangeListener mapProviderListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mapProviderPreference.setValue(newValue.toString());
            Startup.createMap(newValue.toString());
            preference.setSummary(mapProviderPreference.getEntry());
            return true;
        }
    };

    Preference.OnPreferenceChangeListener distanceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = (String) newValue;
            String key = preference.getKey();
            if (value.length() > 6) {
                value = value.substring(0, 6);
            }
            try {
                if (Integer.parseInt(value) > Const.EQUATOR) {
                    value = String.valueOf(Const.EQUATOR);
                }
            } catch (Exception e) {
                value = "200";
            }
            preference.getSharedPreferences().edit().putString(key, value).commit();
            preference.setSummary(value);
            preference.getEditor().putString(key, value).commit();
            preference.getLayoutResource();
            return true;
        }
    };
}
