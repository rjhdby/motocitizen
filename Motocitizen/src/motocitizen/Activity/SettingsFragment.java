package motocitizen.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import motocitizen.main.R;
import motocitizen.startup.MCPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

public class SettingsFragment extends PreferenceFragment {

    private ListPreference mapProviderPreference;
    private Preference nottifDistPreference, nottifAlarmPreference;
    private Preference authPreference, nottifSoundPreference;
    private Preference showAcc, showBreak, showSteal, showOther;
    private MCPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new MCPreferences(getActivity());
        update();
    }

    private void update() {
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        Preference buttonAuth = findPreference(getResources().getString(R.string.mc_settings_auth_button));
        buttonAuth.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(Startup.context, AuthActivity.class);
                Startup.context.startActivity(i);
                return true;
            }
        });
        Preference buttonSound = findPreference(getResources().getString(R.string.mc_notif_sound));
        buttonSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(Startup.context, SelectSoundActivity.class);
                Startup.context.startActivity(i);
                return true;
            }
        });
        nottifDistPreference = findPreference(prefs.distanceShow);
        nottifAlarmPreference = findPreference(prefs.distanceAlarm);

        nottifSoundPreference = findPreference(getResources().getString(R.string.mc_notif_sound));
        authPreference = findPreference(getResources().getString(R.string.mc_settings_auth_button));

        mapProviderPreference = (ListPreference) getPreferenceScreen().findPreference(prefs.mapProvider);
        mapProviderPreference.setOnPreferenceChangeListener(mapProviderListener);
        nottifDistPreference.setOnPreferenceChangeListener(distanceListener);
        nottifAlarmPreference.setOnPreferenceChangeListener(distanceListener);
        authPreference.setSummary(prefs.getLogin());
        nottifSoundPreference.setSummary(prefs.getAlarmSoundTitle());
        nottifDistPreference.setSummary(String.valueOf(prefs.getVisibleDistance()));
        nottifAlarmPreference.setSummary(String.valueOf(prefs.getAlarmDistance()));

        showAcc = findPreference(prefs.showAcc);
        showBreak = findPreference(prefs.showBreak);
        showSteal = findPreference(prefs.showSteal);
        showOther = findPreference(prefs.showOther);

        showAcc.setOnPreferenceChangeListener(visibleListener);
        showBreak.setOnPreferenceChangeListener(visibleListener);
        showSteal.setOnPreferenceChangeListener(visibleListener);
        showOther.setOnPreferenceChangeListener(visibleListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private final Preference.OnPreferenceChangeListener mapProviderListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mapProviderPreference.setValue(newValue.toString());
            Startup.createMap(newValue.toString());
            preference.setSummary(mapProviderPreference.getEntry());
            return true;
        }
    };

    private final Preference.OnPreferenceChangeListener distanceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String valueText = (String) newValue;
            int value;
            String key = preference.getKey();
            if (valueText.length() > 6) {
                valueText = valueText.substring(0, 6);
            }
            value = Integer.parseInt(valueText);
            if (value > Const.EQUATOR) {
                value = Const.EQUATOR;
            }

            if (preference.equals(nottifDistPreference)) {
                prefs.setVisibleDistance(value);
            } else if (preference.equals(nottifAlarmPreference)) {
                prefs.setAlarmDistance(value);
            }
            update();
            return false;
        }
    };

    private final Preference.OnPreferenceChangeListener visibleListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            prefs.putBoolean(preference.getKey(), (boolean) newValue);
            boolean visible = prefs.toShowAcc() | prefs.toShowBreak() | prefs.toShowOther() | prefs.toShowSteal();
            if (!visible) {
                Toast toast = Toast.makeText(Startup.context, getString(R.string.no_one_accident_visible), Toast.LENGTH_LONG);
                toast.show();
            }
            update();
            return false;
        }
    };
}
