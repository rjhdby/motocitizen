package motocitizen.fragments;

import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import motocitizen.activity.AuthActivity;
import motocitizen.main.R;
import motocitizen.user.Auth;
import motocitizen.utils.Const;
import motocitizen.utils.Preferences;
import motocitizen.utils.ShowToast;

public class SettingsFragment extends PreferenceFragment {
    private Preference     notificationDistPreference;
    private Preference     notificationAlarmPreference;
    private ListPreference mapProviderPreference;

    private void update() {
        String login = Preferences.getInstance().getLogin();
        Preference.OnPreferenceChangeListener visibleListener = (preference, newValue) -> {
            Preferences.getInstance().putBoolean(preference.getKey(), (boolean) newValue);
            if (Preferences.getInstance().hideAccidents()
                && Preferences.getInstance().hideBreaks()
                && Preferences.getInstance().hideOthers()
                && Preferences.getInstance().hideSteals()) {
                ShowToast.message(getActivity(), getString(R.string.no_one_accident_visible));
            }
            update();
            return false;
        };
        Preference.OnPreferenceChangeListener distanceListener = (preference, newValue) -> {
            Integer value = Math.min(Const.EQUATOR, Integer.parseInt((String) newValue));

            if (preference.equals(notificationDistPreference)) {
                Preferences.getInstance().setVisibleDistance(value);
            } else if (preference.equals(notificationAlarmPreference)) {
                Preferences.getInstance().setAlarmDistance(value);
            }
            update();
            return false;
        };

        mapProviderPreference = (ListPreference) getPreferenceScreen().findPreference(Preferences.getInstance().getPreferenceName("mapProvider"));
        notificationDistPreference = findPreference(Preferences.getInstance().getPreferenceName("distanceShow"));
        notificationAlarmPreference = findPreference(Preferences.getInstance().getPreferenceName("distanceAlarm"));
        Preference buttonAuth                  = findPreference(getResources().getString(R.string.settings_auth_button));
        Preference buttonSound                 = findPreference(getResources().getString(R.string.notification_sound));
        Preference showAcc                     = findPreference(Preferences.getInstance().getPreferenceName("showAcc"));
        Preference showBreak                   = findPreference(Preferences.getInstance().getPreferenceName("showBreak"));
        Preference showSteal                   = findPreference(Preferences.getInstance().getPreferenceName("showSteal"));
        Preference showOther                   = findPreference(Preferences.getInstance().getPreferenceName("showOther"));
        Preference hoursAgo                    = findPreference(Preferences.getInstance().getPreferenceName("hoursAgo"));
        Preference maxNotifications            = findPreference(Preferences.getInstance().getPreferenceName("maxNotifications"));
        Preference useVibration                = findPreference(Preferences.getInstance().getPreferenceName("useVibration"));
        Preference notificationSoundPreference = findPreference(getResources().getString(R.string.notification_sound));
        Preference authPreference              = findPreference(getResources().getString(R.string.settings_auth_button));

        authPreference.setSummary(login.length() > 0 ? Auth.getInstance().getRole().getName() + ": " + login : Auth.getInstance().getRole().getName());
        maxNotifications.setSummary(String.valueOf(Preferences.getInstance().getMaxNotifications()));
        hoursAgo.setSummary(String.valueOf(Preferences.getInstance().getHoursAgo()));
        notificationSoundPreference.setSummary(Preferences.getInstance().getAlarmSoundTitle());
        notificationDistPreference.setSummary(String.valueOf(Preferences.getInstance().getVisibleDistance()));
        notificationAlarmPreference.setSummary(String.valueOf(Preferences.getInstance().getAlarmDistance()));

        notificationDistPreference.setOnPreferenceChangeListener(distanceListener);
        notificationAlarmPreference.setOnPreferenceChangeListener(distanceListener);
        mapProviderPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            mapProviderPreference.setValue(newValue.toString());
            preference.setSummary(mapProviderPreference.getEntry());
            return true;
        });
        maxNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
//            if (((String) newValue).startsWith("-")) newValue = "0";
            preference.setSummary((String) newValue);
            return true;
        });
        hoursAgo.setOnPreferenceChangeListener((preference, newValue) -> {
            Integer value = Integer.parseInt((String) newValue);
//            if (value > 24) newValue = "24";
//            if (value < 1) newValue = "1";
            if (newValue.equals("0")) newValue = "1";
            preference.setSummary(newValue.toString());
            return true;
        });
        useVibration.setOnPreferenceChangeListener((preference, newValue) -> {
            Preferences.getInstance().putBoolean(preference.getKey(), (boolean) newValue);
            return true;
        });
        buttonSound.setOnPreferenceClickListener(stub -> {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SelectSoundFragment()).commit();
            return true;
        });
        buttonAuth.setOnPreferenceClickListener(stub -> {
            getActivity().startActivity(new Intent(getActivity(), AuthActivity.class));
            return true;
        });
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
}
