package motocitizen.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

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

    private SharedPreferences prefs;
    private ListPreference mapProviderPreference;
    private Preference nottifDistPreference;
    private Preference nottifAlarmPreference;

    //TODO вызывать setSummary при изменении значений дистанций оповещений

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String) newValue;
        String key = preference.getKey();
        if(value.length()>6){
            value = value.substring(0,6);
        }
        try {
            if (Integer.parseInt(value) > Const.EQUATOR) {
                value = String.valueOf(Const.EQUATOR);
            }
        } catch(Exception e){
            value = "200";
        }
        preference.setSummary(value);
        preference.getEditor().putString(key, value).commit();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //TODO Сделать однообразно
        Preference authPreference = (Preference) findPreference(getResources().getString(R.string.mc_settings_auth_button));
        authPreference.setSummary(prefs.getString("mc.login", ""));

        Preference nottifSoundPreference = (Preference) findPreference(getResources().getString(R.string.mc_notif_sound));
        nottifSoundPreference.setSummary(prefs.getString("mc.notification.sound.title", getString(R.string.mc_notif_system)));

        nottifDistPreference = (Preference) findPreference(MC_DISTANCE_SHOW);
        nottifDistPreference.setSummary(prefs.getString("mc.distance.show", "200"));

        nottifAlarmPreference = (Preference) findPreference(MC_DISTANCE_ALARM);
        nottifAlarmPreference.setSummary(prefs.getString("mc.distance.alarm", "20"));

        nottifDistPreference.setOnPreferenceChangeListener(this);
        nottifAlarmPreference.setOnPreferenceChangeListener(this);
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

        mapProviderPreference = (ListPreference)getPreferenceScreen().findPreference("map_pref");
        mapProviderPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mapProviderPreference.setValue(newValue.toString());
                Startup.createMap(newValue.toString());
                preference.setSummary(mapProviderPreference.getEntry());
                return true;
            }
        });
        return;
    }

    public static int getAlarmDistance(SharedPreferences prefs) {
        String periodString = prefs.getString(MC_DISTANCE_ALARM, "100");
        int res = 100;

        try {
            res = Integer.parseInt(periodString);
        } catch (Exception e) {
        }
        return res;
    }

    public static String getShowDistance(SharedPreferences prefs) {
        return prefs.getString(MC_DISTANCE_SHOW, "100");
    }

    public static Boolean isShowAcc(SharedPreferences prefs) {
        return prefs.getBoolean(MC_SHOW_ACC, true);
    }

    public static Boolean isShowBreak(SharedPreferences prefs) {
        return prefs.getBoolean(MC_SHOW_BREAK, true);
    }

    public static Boolean isShowSteal(SharedPreferences prefs) {
        return prefs.getBoolean(MC_SHOW_STEAL, true);
    }
    public static Boolean isShowOther(SharedPreferences prefs) {
        return prefs.getBoolean(MC_SHOW_OTHER, true);
    }
}
