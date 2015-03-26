package motocitizen.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Button;

import motocitizen.main.R;

/**
 * Created by elagin on 26.03.15.
 */
public class ConfigActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    public static final String MC_DISTANCE_SHOW = "mc.distance.show";
    public static final String MC_DISTANCE_ALARM = "mc.distance.alarm";

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        //setContentView(R.layout.main);

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
}
