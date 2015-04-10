package motocitizen.utils;

import android.content.SharedPreferences;

@SuppressWarnings("WeakerAccess")
public class Configuration {
    public static final String MC_DISTANCE_SHOW = "mc.distance.show";
    public static final String MC_DISTANCE_ALARM = "mc.distance.alarm";
    public static final String MC_SHOW_BREAK = "mc.show.break";
    public static final String MC_SHOW_ACC = "mc.show.acc";
    public static final String MC_SHOW_STEAL = "mc.show.steal";
    public static final String MC_SHOW_OTHER = "mc.show.other";

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

    public static int getAlarmDistance(SharedPreferences prefs) {
        String periodString = prefs.getString(MC_DISTANCE_ALARM, "100");
        int res;

        try {
            res = Integer.parseInt(periodString);
        } catch (Exception e) {
            res = 100;
        }
        return res;
    }
}
