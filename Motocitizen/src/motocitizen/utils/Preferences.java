package motocitizen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.dictionary.Type;

@SuppressLint("CommitPrefEdits")
public class Preferences {
    private              boolean newVersion                = false;
    /* constants */
    public final static  float   DEFAULT_LATITUDE          = 55.752295f;
    public final static  float   DEFAULT_LONGITUDE         = 37.622735f;
    private final static int     DEFAULT_SHOW_DISTANCE     = 200;
    private final static int     DEFAULT_ALARM_DISTANCE    = 20;
    private final static int     DEFAULT_MAX_NOTIFICATIONS = 3;
    private final static int     DEFAULT_MAX_AGE           = 24;
    private final static boolean DEFAULT_VIBRATION         = true;
    private final static boolean DEFAULT_DO_NOT_DISTURB    = false;
    private final static boolean DEFAULT_IS_ANONYMOUS      = false;
    private final static boolean DEFAULT_SHOW_TYPE         = true;
    /* end constants */

    private SharedPreferences preferences;

    private final String showAcc             = "mc.show.acc";
    private final String showBreak           = "mc.show.break";
    private final String showSteal           = "mc.show.steal";
    private final String showOther           = "mc.show.other";
    private final String distanceShow        = "mc.distance.show";
    private final String distanceAlarm       = "mc.distance.alarm";
    private final String mapProvider         = "mc.map.provider";
    private final String doNotDisturb        = "do.not.disturb";
    private final String hoursAgo            = "hours.ago";
    private final String maxNotifications    = "notifications.max";
    private final String useVibration        = "use.vibration";
    private final String userId              = "userId";
    private final String userName            = "userName";
    private final String userRole            = "userRole";
    private final String onWay               = "mc.onway";
    private final String soundTitle          = "mc.notification.sound.title";
    private final String soundURI            = "mc.notification.sound";
    private final String login               = "mc.login";
    private final String password            = "mc.password";
    private final String anonim              = "mc.anonim";
    private final String GCMRegistrationCode = "mc.gcm.id";
    private final String appVersion          = "mc.app.version";
    private final String GcmAppVersion       = "gcm.app.version";
    private final String savedLng            = "savedlng";
    private final String savedLat            = "savedlat";

    private static class Holder {
        private static Preferences instance;
    }

    public static Preferences getInstance(Context context) {
        if (Holder.instance == null) {
            Holder.instance = new Preferences(context);
            Holder.instance.setDoNotDisturb(false);
        }
        return Holder.instance;
    }

    @Nullable
    public static Preferences dirtyRead() {
        return Holder.instance;
    }

    private Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        initAlarmSoundUri(context);
        try {
            int version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            if (version != getAppVersion()) {
                setNewVersion();
                setAppVersion(version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void putBoolean(String name, boolean value) {
        preferences.edit().putBoolean(name, value).commit();
    }

    public int getOnWay() {
        return preferences.getInt(onWay, 0);
    }

    public void setOnWay(int id) {
        preferences.edit().putInt(onWay, id).commit();
    }

    public LatLng getSavedLatLng() {
        double lat = (double) preferences.getFloat(savedLat, DEFAULT_LATITUDE);
        double lng = (double) preferences.getFloat(savedLng, DEFAULT_LONGITUDE);
        return new LatLng(lat, lng);
    }

    public boolean getDoNotDisturb() {
        return preferences.getBoolean(doNotDisturb, DEFAULT_DO_NOT_DISTURB);
    }

    public void setDoNotDisturb(boolean value) {
        preferences.edit().putBoolean(doNotDisturb, value).commit();
    }

    public void saveLatLng(LatLng latlng) {
        preferences.edit().putFloat(savedLat, (float) latlng.latitude).putFloat(savedLng, (float) latlng.longitude).commit();
    }

    public int getVisibleDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceShow, DEFAULT_SHOW_DISTANCE);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceShow, String.valueOf(DEFAULT_SHOW_DISTANCE));
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public void setVisibleDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putString(distanceShow, String.valueOf(distance)).commit();
    }

    public int getAlarmDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceAlarm, DEFAULT_ALARM_DISTANCE);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceAlarm, String.valueOf(DEFAULT_ALARM_DISTANCE));
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public void setAlarmDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putString(distanceAlarm, String.valueOf(distance)).commit();
    }

    public String getAlarmSoundTitle() {
        return preferences.getString(soundTitle, "default system");
    }

    private Uri alarmSoundUri;

    public void initAlarmSoundUri(Context context) {
        String uriString = preferences.getString(soundURI, "default");
        if (uriString.equals("default")) {
            alarmSoundUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        } else alarmSoundUri = Uri.parse(uriString);
    }

    public Uri getAlarmSoundUri() {

        return alarmSoundUri;
    }

    public String getLogin() {
        return preferences.getString(login, "");
    }

    public void setLogin(String value) {
        preferences.edit().putString(login, value).commit();
    }

    public String getPassword() {
        return preferences.getString(password, "");
    }

    public void setPassword(String value) {
        preferences.edit().putString(password, value).commit();
    }

    public boolean isAnonim() {
        return preferences.getBoolean(anonim, DEFAULT_IS_ANONYMOUS);
    }

    public void setAnonim(boolean value) {
        preferences.edit().putBoolean(anonim, value).commit();
    }

    public String getGCMRegistrationCode() {
        if (getAppVersion() != preferences.getInt(GcmAppVersion, -1)) return "";
        return preferences.getString(GCMRegistrationCode, "");
    }

    public void setGCMRegistrationCode(String code) {
        preferences.edit().putInt(GcmAppVersion, getAppVersion());
        preferences.edit().putString(GCMRegistrationCode, code).commit();
    }

    public int getAppVersion() {
        return preferences.getInt(appVersion, 0);
    }

    public void setAppVersion(int code) {
        preferences.edit().putInt(appVersion, code).commit();
    }

    public void setSoundAlarm(String title, Uri uri) {
        preferences.edit().putString(soundTitle, title).putString(soundURI, uri.toString()).commit();
    }

    public void setDefaultSoundAlarm() {
        preferences.edit().putString(soundTitle, "default system").putString(soundURI, "default").commit();
    }

    public void resetAuth() {
        preferences.edit().remove(login).remove(password).commit();
    }

    public boolean isHidden(Type type) {
        switch (type) {
            case BREAK:
                return hideBreaks();
            case MOTO_AUTO:
            case MOTO_MOTO:
            case MOTO_MAN:
            case SOLO:
                return hideAccidents();
            case STEAL:
                return hideSteals();
            case OTHER:
            default:
                return hideOthers();
        }
    }

    public boolean hideBreaks() {
        return !preferences.getBoolean(showBreak, DEFAULT_SHOW_TYPE);
    }

    public boolean hideAccidents() {
        return !preferences.getBoolean(showAcc, DEFAULT_SHOW_TYPE);
    }

    public boolean hideSteals() {
        return !preferences.getBoolean(showSteal, DEFAULT_SHOW_TYPE);
    }

    public boolean hideOthers() {
        return !preferences.getBoolean(showOther, DEFAULT_SHOW_TYPE);
    }

    public int getMaxNotifications() {
        return Integer.parseInt(preferences.getString(maxNotifications, String.valueOf(DEFAULT_MAX_NOTIFICATIONS)));
    }

    public String getPreferenceName(String preference) {
        switch (preference) {
            case "hoursAgo":
                return hoursAgo;
            case "showAcc":
                return showAcc;
            case "showBreak":
                return showBreak;
            case "showSteal":
                return showSteal;
            case "showOther":
                return showOther;
            case "distanceShow":
                return distanceShow;
            case "distanceAlarm":
                return distanceAlarm;
            case "mapProvider":
                return mapProvider;
            case "maxNotifications":
                return maxNotifications;
            case "useVibration":
                return useVibration;
            default:
                return "unknown";
        }
    }

    public int getHoursAgo() {
        return Integer.parseInt(preferences.getString(hoursAgo, String.valueOf(DEFAULT_MAX_AGE)));
    }

    public boolean getVibration() {
        return preferences.getBoolean(useVibration, DEFAULT_VIBRATION);
    }

    public String getUserName() {
        return preferences.getString(userName, "");
    }

    public void setUserName(String name) {
        preferences.edit().putString(userName, name).commit();
    }

    public int getUserId() {
        return preferences.getInt(userId, 0);
    }

    public void setUserId(int id) {
        preferences.edit().putInt(userId, id).commit();
    }

    public void setUserRole(String role) {
        preferences.edit().putString(userRole, role).commit();
    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public void setNewVersion() {
        newVersion = true;
    }

    public void resetNewVersion() {
        newVersion = false;
    }
}
