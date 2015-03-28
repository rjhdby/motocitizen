package motocitizen.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 26.03.15.
 */
public class ConfigActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    public static final String MC_DISTANCE_SHOW = "mc.distance.show";
    public static final String MC_DISTANCE_ALARM = "mc.distance.alarm";
    public static final String MC_SHOW_BREAK = "mc.show.break";
    public static final String MC_SHOW_ACC = "mc.show.acc";
    public static final String MC_SHOW_STEAL = "mc.show.steal";
    public static final String MC_SHOW_OTHER = "mc.show.other";

    //TODO вызывать setSummary при изменении значений дистанций оповещений

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO Сделать однообразно
        Preference authPreference = (Preference) findPreference(getResources().getString(R.string.mc_settings_auth_button));
        authPreference.setSummary(Startup.prefs.getString("mc.login", ""));

        Preference nottifSoundPreference = (Preference) findPreference(getResources().getString(R.string.mc_notif_sound));
        nottifSoundPreference.setSummary(Startup.prefs.getString("mc.notification.sound.title", getString(R.string.mc_notif_system)));

        Preference nottifDistPreference = (Preference) findPreference(MC_DISTANCE_SHOW);
        nottifDistPreference.setSummary(Startup.prefs.getString("mc.distance.show", "0"));

        Preference nottifAlarmPreference = (Preference) findPreference(MC_DISTANCE_ALARM);
        nottifAlarmPreference.setSummary(Startup.prefs.getString("mc.distance.alarm", "0"));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference buttonAuth = (Preference)findPreference(getResources().getString(R.string.mc_settings_auth_button));
        buttonAuth.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(Startup.context, AuthActivity.class);
                Startup.context.startActivity(i);

//                String login = Startup.prefsDef.getString("mc_login", "");
//                String password = Startup.prefsDef.getString("mc_password", "");
//                Text.set(R.id.mc_auth_login, login);
//                Text.set(R.id.mc_auth_password, password);
//                Show.show(R.id.mc_auth);
                return true;
            }
        });

        Preference buttonSound = (Preference)findPreference(getResources().getString(R.string.mc_notif_sound));
        buttonSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(Startup.context, SelectSoundActivity.class);
                Startup.context.startActivity(i);
                //new MCSelectSound(Startup.context);
                return true;
            }
        });

        return;
    }

    public static int getAlarmDistance(SharedPreferences prefs) {
        String periodString = prefs.getString(ConfigActivity.MC_DISTANCE_ALARM, "100");
        int res = 100;

        try {
            res = Integer.parseInt(periodString);
        } catch (Exception e) {
        }
        return res;
    }

    public static String getShowDistance(SharedPreferences prefs) {
        return prefs.getString(ConfigActivity.MC_DISTANCE_SHOW, "100");
    }

    public static Boolean isShowAcc(SharedPreferences prefs) {
        return prefs.getBoolean(ConfigActivity.MC_SHOW_ACC, true);
    }

    public static Boolean isShowBreak(SharedPreferences prefs) {
        return prefs.getBoolean(ConfigActivity.MC_SHOW_BREAK, true);
    }

    public static Boolean isShowSteal(SharedPreferences prefs) {
        return prefs.getBoolean(ConfigActivity.MC_SHOW_STEAL, true);
    }
    public static Boolean isShowOther(SharedPreferences prefs) {
        return prefs.getBoolean(ConfigActivity.MC_SHOW_OTHER, true);
    }
}
