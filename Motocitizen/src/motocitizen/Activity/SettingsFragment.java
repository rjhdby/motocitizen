package motocitizen.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.startup.Preferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

public class SettingsFragment extends PreferenceFragment {

    private static Activity act;
    Preference
            nottifDistPreference,
            nottifAlarmPreference,
            authPreference,
            nottifSoundPreference,
            showAcc,
            showBreak,
            showSteal,
            showOther,
            hoursAgo,
            maxNotifications, useVibration;
    private ListPreference mapProviderPreference;
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
            if (valueText.length() > 6) {
                valueText = valueText.substring(0, 6);
            }
            value = Integer.parseInt(valueText);
            value = Math.max(Const.EQUATOR, value);

            if (preference.equals(nottifDistPreference)) {
                Preferences.setVisibleDistance(value);
            } else if (preference.equals(nottifAlarmPreference)) {
                Preferences.setAlarmDistance(value);
            }
            update();
            return false;
        }
    };
    private final Preference.OnPreferenceChangeListener visibleListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.putBoolean(preference.getKey(), (boolean) newValue);
            boolean visible = Preferences.toShowAcc() | Preferences.toShowBreak() | Preferences.toShowOther() | Preferences.toShowSteal();
            if (!visible) {
                message(getString(R.string.no_one_accident_visible));
            }
            update();
            return false;
        }
    };
    private Preferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = getActivity();
        prefs = new Preferences(act);
        update();
    }

    private void update() {
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        Preference buttonAuth = findPreference(getResources().getString(R.string.mc_settings_auth_button));
        buttonAuth.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(act, AuthActivity.class);
                act.startActivity(i);
                return true;
            }
        });
        Preference buttonSound = findPreference(getResources().getString(R.string.mc_notif_sound));
        buttonSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(act, SelectSoundActivity.class);
                act.startActivity(i);
                return true;
            }
        });
        nottifDistPreference = findPreference(Preferences.distanceShow);
        nottifAlarmPreference = findPreference(Preferences.distanceAlarm);

        nottifSoundPreference = findPreference(getResources().getString(R.string.mc_notif_sound));
        authPreference = findPreference(getResources().getString(R.string.mc_settings_auth_button));

        mapProviderPreference = (ListPreference) getPreferenceScreen().findPreference(Preferences.mapProvider);
        mapProviderPreference.setOnPreferenceChangeListener(mapProviderListener);
        nottifDistPreference.setOnPreferenceChangeListener(distanceListener);
        nottifAlarmPreference.setOnPreferenceChangeListener(distanceListener);
        String login = Preferences.getLogin();
        if (login.length() > 0)
            authPreference.setSummary(Role.getName(getActivity()) + ": " + login);
        else
            authPreference.setSummary(Role.getName(getActivity()));
        nottifSoundPreference.setSummary(Preferences.getAlarmSoundTitle());
        nottifDistPreference.setSummary(String.valueOf(Preferences.getVisibleDistance()));
        nottifAlarmPreference.setSummary(String.valueOf(Preferences.getAlarmDistance()));

        showAcc = findPreference(Preferences.showAcc);
        showBreak = findPreference(Preferences.showBreak);
        showSteal = findPreference(Preferences.showSteal);
        showOther = findPreference(Preferences.showOther);

        showAcc.setOnPreferenceChangeListener(visibleListener);
        showBreak.setOnPreferenceChangeListener(visibleListener);
        showSteal.setOnPreferenceChangeListener(visibleListener);
        showOther.setOnPreferenceChangeListener(visibleListener);

        hoursAgo = findPreference(Preferences.hoursAgo);
        hoursAgo.setSummary(String.valueOf(Preferences.getHoursAgo()));
        hoursAgo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Integer value;
                try {
                    value = Integer.parseInt((String) newValue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                if (value > 24) newValue = "24";
                if (value < 1) newValue = "1";
                preference.setSummary(newValue.toString());
                return true;
            }
        });
        maxNotifications = findPreference(Preferences.maxNotifications);
        maxNotifications.setSummary(String.valueOf(Preferences.getMaxNotifications()));
        maxNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    if (Integer.parseInt((String) newValue) < 0) newValue = "0";
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                preference.setSummary(newValue.toString());
                return true;
            }
        });
        useVibration = findPreference(Preferences.useVibration);
        useVibration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Preferences.putBoolean(preference.getKey(), (boolean) newValue);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void message(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }
}
