package motocitizen.ui.fragments;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.user.User;
import motocitizen.utils.LocationUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.ToastUtils;

public class SettingsFragment extends PreferenceFragment {
    private static final int PREFERENCES = R.xml.preferences;

    private Preference notificationDistPreference;
    private Preference notificationAlarmPreference;
    private final Preferences preferences = Preferences.INSTANCE;

    private Preference buttonAuth;
    private Preference buttonSound;
    private Preference showAcc;
    private Preference showBreak;
    private Preference showSteal;
    private Preference showOther;
    private Preference hoursAgo;
    private Preference maxNotifications;
    private Preference useVibration;
    private Preference notificationSoundPreference;
    private Preference authPreference;

    String login = preferences.getLogin();

    @Override
    public void onResume() {
        super.onResume();
        setPreferenceScreen(null);
        addPreferencesFromResource(PREFERENCES);
        bindPreferences();
        setUpListeners();
        update();
    }

    private void update() {
        authPreference.setSummary(login.length() > 0 ? User.INSTANCE.getRoleName() + ": " + login : User.INSTANCE.getRoleName());
        maxNotifications.setSummary(String.valueOf(preferences.getMaxNotifications()));
        hoursAgo.setSummary(String.valueOf(preferences.getHoursAgo()));
        notificationSoundPreference.setSummary(preferences.getSoundTitle());
        notificationDistPreference.setSummary(String.valueOf(preferences.getVisibleDistance()));
        notificationAlarmPreference.setSummary(String.valueOf(preferences.getAlarmDistance()));
    }

    private void setUpListeners() {
        notificationDistPreference.setOnPreferenceChangeListener(this::distanceListener);
        notificationAlarmPreference.setOnPreferenceChangeListener(this::distanceListener);
        maxNotifications.setOnPreferenceChangeListener(this::maxNotificationsListener);
        hoursAgo.setOnPreferenceChangeListener(this::hoursAgoListener);
        useVibration.setOnPreferenceChangeListener(this::vibrationListener);
        buttonSound.setOnPreferenceClickListener(this::soundButtonPressed);
        buttonAuth.setOnPreferenceClickListener(this::authButtonPressed);
        showAcc.setOnPreferenceChangeListener(this::visibleListener);
        showBreak.setOnPreferenceChangeListener(this::visibleListener);
        showSteal.setOnPreferenceChangeListener(this::visibleListener);
        showOther.setOnPreferenceChangeListener(this::visibleListener);
    }

    private boolean maxNotificationsListener(Preference preference, Object newValue) {
        preference.setSummary((String) newValue);
        return true;
    }

    private boolean hoursAgoListener(Preference preference, Object newValue) {
        if (newValue.equals("0")) newValue = "1";
        preference.setSummary(newValue.toString());
        return true;
    }

    private boolean vibrationListener(Preference preference, Object newValue) {
        preferences.putBoolean(preference.getKey(), (boolean) newValue);
        return true;
    }

    private boolean authButtonPressed(Preference stub) {
        Router.INSTANCE.goTo(getActivity(), Router.Target.AUTH);
        return true;
    }

    private boolean soundButtonPressed(Preference stub) {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SelectSoundFragment()).commit();
        return true;
    }

    private void bindPreferences() {
        notificationDistPreference = findPreference(preferences.getPreferenceName("distanceShow"));
        notificationAlarmPreference = findPreference(preferences.getPreferenceName("distanceAlarm"));
        buttonAuth = findPreference(getResources().getString(R.string.settings_auth_button));
        buttonSound = findPreference(getResources().getString(R.string.notification_sound));
        showAcc = findPreference(preferences.getPreferenceName("showAcc"));
        showBreak = findPreference(preferences.getPreferenceName("showBreak"));
        showSteal = findPreference(preferences.getPreferenceName("showSteal"));
        showOther = findPreference(preferences.getPreferenceName("showOther"));
        hoursAgo = findPreference(preferences.getPreferenceName("hoursAgo"));
        maxNotifications = findPreference(preferences.getPreferenceName("maxNotifications"));
        useVibration = findPreference(preferences.getPreferenceName("useVibration"));
        notificationSoundPreference = findPreference(getResources().getString(R.string.notification_sound));
        authPreference = findPreference(getResources().getString(R.string.settings_auth_button));
    }

    @NonNull
    private boolean distanceListener(Preference preference, Object newValue) {
        Integer value = Math.min(LocationUtils.getEQUATOR(), Integer.parseInt((String) newValue));

        if (preference.equals(notificationDistPreference)) {
            preferences.setVisibleDistance(value);
        } else if (preference.equals(notificationAlarmPreference)) {
            preferences.setAlarmDistance(value);
        }
        update();
        return false;
    }

    @NonNull
    private boolean visibleListener(Preference preference, Object newValue) {
        preferences.putBoolean(preference.getKey(), (boolean) newValue);
        if (isAllHidden()) {
            ToastUtils.show(getActivity(), getString(R.string.no_one_accident_visible));
        }
        update();
        return false;
    }

    private boolean isAllHidden() {
        return !(preferences.getShowAcc() || preferences.getShowBreak() || preferences.getShowOther() || preferences.getShowSteal());
    }
}
