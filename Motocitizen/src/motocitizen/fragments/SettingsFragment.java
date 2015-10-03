package motocitizen.fragments;

import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import motocitizen.Activity.AuthActivity;
import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.utils.Const;
import motocitizen.utils.Preferences;

public class SettingsFragment extends PreferenceFragment {
    private Preference     nottifDistPreference;
    private Preference     nottifAlarmPreference;
    private ListPreference mapProviderPreference;

    private void update() {
        String                                login            = Preferences.getLogin();
        Preference.OnPreferenceChangeListener visibleListener  = new VisibleChangeListener();
        Preference.OnPreferenceChangeListener distanceListener = new DistanceChangeListener();

        mapProviderPreference = (ListPreference) getPreferenceScreen().findPreference(Preferences.mapProvider);
        nottifDistPreference = findPreference(Preferences.distanceShow);
        nottifAlarmPreference = findPreference(Preferences.distanceAlarm);
        Preference buttonAuth            = findPreference(getResources().getString(R.string.settings_auth_button));
        Preference buttonSound           = findPreference(getResources().getString(R.string.notification_sound));
        Preference showAcc               = findPreference(Preferences.showAcc);
        Preference showBreak             = findPreference(Preferences.showBreak);
        Preference showSteal             = findPreference(Preferences.showSteal);
        Preference showOther             = findPreference(Preferences.showOther);
        Preference hoursAgo              = findPreference(Preferences.hoursAgo);
        Preference maxNotifications      = findPreference(Preferences.maxNotifications);
        Preference useVibration          = findPreference(Preferences.useVibration);
        Preference nottifSoundPreference = findPreference(getResources().getString(R.string.notification_sound));
        Preference authPreference        = findPreference(getResources().getString(R.string.settings_auth_button));

        authPreference.setSummary(login.length() > 0 ? MyApp.getRole().getName() + ": " + login : MyApp.getRole().getName());
        maxNotifications.setSummary(String.valueOf(Preferences.getMaxNotifications()));
        hoursAgo.setSummary(String.valueOf(Preferences.getHoursAgo()));
        nottifSoundPreference.setSummary(Preferences.getAlarmSoundTitle());
        nottifDistPreference.setSummary(String.valueOf(Preferences.getVisibleDistance()));
        nottifAlarmPreference.setSummary(String.valueOf(Preferences.getAlarmDistance()));

        nottifDistPreference.setOnPreferenceChangeListener(distanceListener);
        nottifAlarmPreference.setOnPreferenceChangeListener(distanceListener);
        mapProviderPreference.setOnPreferenceChangeListener(new MapProviderChangeListener());
        maxNotifications.setOnPreferenceChangeListener(new MaxNotificationsChangeListener());
        hoursAgo.setOnPreferenceChangeListener(new HoursAgoChangeListener());
        useVibration.setOnPreferenceChangeListener(new VibrationChangeListener());
        buttonSound.setOnPreferenceClickListener(new SelectSoundButtonListener());
        buttonAuth.setOnPreferenceClickListener(new AuthButtonListener());
        showAcc.setOnPreferenceChangeListener(visibleListener);
        showBreak.setOnPreferenceChangeListener(visibleListener);
        showSteal.setOnPreferenceChangeListener(visibleListener);
        showOther.setOnPreferenceChangeListener(visibleListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        update();
    }

    private void message(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    private class HoursAgoChangeListener implements Preference.OnPreferenceChangeListener {
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
    }

    private class MaxNotificationsChangeListener implements Preference.OnPreferenceChangeListener {
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
    }

    private class VibrationChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.putBoolean(preference.getKey(), (boolean) newValue);
            return true;
        }
    }

    private class DistanceChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String valueText = (String) newValue;
            int    value;
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
    }

    private class VisibleChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.putBoolean(preference.getKey(), (boolean) newValue);
            if (!(!Preferences.hideAccidents() || !Preferences.hideBreaks() || !Preferences.hideOthers() || !Preferences.hideSteals())) {
                message(getString(R.string.no_one_accident_visible));
            }
            update();
            return false;
        }
    }

    private class AuthButtonListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference arg0) {
            Intent i = new Intent(getActivity(), AuthActivity.class);
            getActivity().startActivity(i);
            return true;
        }
    }

    private class SelectSoundButtonListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference arg0) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SelectSoundFragment()).commit();
            return true;
        }
    }

    private class MapProviderChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mapProviderPreference.setValue(newValue.toString());
            preference.setSummary(mapProviderPreference.getEntry());
            return true;
        }
    }
}
