package motocitizen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

import motocitizen.MyApp;
import motocitizen.content.Type;
import motocitizen.main.R;

@SuppressLint("CommitPrefEdits")
public class Preferences {
    private static       boolean newVersion                = false;
    /* constants */
    private final static float   DEFAULT_LATITUDE          = 55.752295f;
    private final static float   DEFAULT_LONGITUDE         = 37.622735f;
    private final static int     DEFAULT_SHOW_DISTANCE     = 200;
    private final static int     DEFAULT_ALARM_DISTANCE    = 20;
    private final static int     DEFAULT_MAX_NOTIFICATIONS = 3;
    private final static int     DEFAULT_MAX_AGE           = 24;
    private final static boolean DEFAULT_VIBRATION         = true;
    private final static boolean DEFAULT_DO_NOT_DISTURB    = false;
    private final static boolean DEFAULT_IS_ANONYMOUS      = false;
    private final static boolean DEFAULT_SHOW_TYPE         = true;
    /* end constants */

    public final static  String showAcc;
    public final static  String showBreak;
    public final static  String showSteal;
    public final static  String showOther;
    public final static  String distanceShow;
    public final static  String distanceAlarm;
    public final static  String mapProvider;
    public final static  String hoursAgo;
    public final static  String maxNotifications;
    public final static  String useVibration;
    private final static String doNotDisturb;
    private final static String userId;
    private final static String userName;
    private final static String userRole;
    private final static String onWay;
    private final static String soundTitle;
    private final static String soundURI;
    private final static String login;
    private final static String password;
    private final static String anonim;
    private final static String GCMRegistrationCode;
    private final static String appVersion;
    private final static String GCMappVersion;
    private final static String savedLng;
    private final static String savedLat;
    private final static String notificationList;

    private final static String[] mapProviders;

    private static SharedPreferences preferences;

    static {
        showAcc = "mc.show.acc";
        showBreak = "mc.show.break";
        showSteal = "mc.show.steal";
        showOther = "mc.show.other";
        distanceShow = "mc.distance.show";
        distanceAlarm = "mc.distance.alarm";
        mapProvider = "mc.map.provider";
        doNotDisturb = "do.not.disturb";
        hoursAgo = "hours.ago";
        maxNotifications = "notifications.max";
        useVibration = "use.vibration";
        userId = "userId";
        userName = "userName";
        userRole = "userRole";
        onWay = "mc.onway";
        soundTitle = "mc.notification.sound.title";
        soundURI = "mc.notification.sound";
        login = "mc.login";
        password = "mc.password";
        anonim = "mc.anonim";
        GCMRegistrationCode = "mc.gcm.id";
        appVersion = "mc.app.version";
        GCMappVersion = "gcm.app.version";
        savedLng = "savedlng";
        savedLat = "savedlat";
        notificationList = "notificationList";

        mapProviders = new String[]{"google", "osm", "yandex"};
    }

    public Preferences(Context context) {
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

    public static void putBoolean(String name, boolean value) {
        preferences.edit().putBoolean(name, value).commit();
    }

    public static int getOnWay() {
        return preferences.getInt(onWay, 0);
    }

    public static void setOnWay(int id) {
        preferences.edit().putInt(onWay, id).commit();
    }

    public static LatLng getSavedLatLng() {
        double lat = (double) preferences.getFloat(savedLat, DEFAULT_LATITUDE);
        double lng = (double) preferences.getFloat(savedLng, DEFAULT_LONGITUDE);
        return new LatLng(lat, lng);
    }

    public static boolean getDoNotDisturb() {
        return preferences.getBoolean(doNotDisturb, DEFAULT_DO_NOT_DISTURB);
    }

    public static void setDoNotDisturb(boolean value) {
        preferences.edit().putBoolean(doNotDisturb, value).commit();
    }

    public static void saveLatLng(LatLng latlng) {
        preferences.edit().putFloat(savedLat, (float) latlng.latitude).putFloat(savedLng, (float) latlng.longitude).commit();
    }

    public static int getVisibleDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceShow, DEFAULT_SHOW_DISTANCE);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceShow, String.valueOf(DEFAULT_SHOW_DISTANCE));
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public static void setVisibleDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putString(distanceShow, String.valueOf(distance)).commit();
    }

    public static int getAlarmDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceAlarm, DEFAULT_ALARM_DISTANCE);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceAlarm, String.valueOf(DEFAULT_ALARM_DISTANCE));
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public static void setAlarmDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putString(distanceAlarm, String.valueOf(distance)).commit();
    }

    public static String getAlarmSoundTitle() {
        return preferences.getString(soundTitle, "default system");
    }

    private static Uri alarmSoundUri;

    public static void initAlarmSoundUri(Context context) {
        String uriString = preferences.getString(soundURI, "default");
        if (uriString.equals("default")) {
            alarmSoundUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        } else alarmSoundUri = Uri.parse(uriString);
    }

    public static Uri getAlarmSoundUri() {

        return alarmSoundUri;
    }

    public static String getLogin() {
        return preferences.getString(login, "");
    }

    public static void setLogin(String value) {
        preferences.edit().putString(login, value).commit();
    }

    public static String getPassword() {
        return preferences.getString(password, "");
    }

    public static void setPassword(String value) {
        preferences.edit().putString(password, value).commit();
    }

    public static boolean isAnonim() {
        return preferences.getBoolean(anonim, DEFAULT_IS_ANONYMOUS);
    }

    public static void setAnonim(boolean value) {
        preferences.edit().putBoolean(anonim, value).commit();
    }

    public static String getGCMRegistrationCode() {
        if (getAppVersion() != preferences.getInt(GCMappVersion, -1)) return "";
        return preferences.getString(GCMRegistrationCode, "");
    }

    public static void setGCMRegistrationCode(String code) {
        preferences.edit().putInt(GCMappVersion, getAppVersion());
        preferences.edit().putString(GCMRegistrationCode, code).commit();
    }

    //TODO ????? ??????????? ??????????
    public static int getAppVersion() {
        return preferences.getInt(appVersion, 0);
    }

    public static void setAppVersion(int code) {
        preferences.edit().putInt(appVersion, code).commit();
    }

    public static void setSoundAlarm(String title, Uri uri) {
        preferences.edit().putString(soundTitle, title).putString(soundURI, uri.toString()).commit();
    }

    public static void setDefaultSoundAlarm() {
        preferences.edit().putString(soundTitle, "default system").putString(soundURI, "default").commit();
    }

    public static void resetAuth() {
        preferences.edit().remove(login).remove(password).commit();
    }

    public static boolean isHidden(Type type) {
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

    public static boolean hideBreaks() {
        return !preferences.getBoolean(showBreak, DEFAULT_SHOW_TYPE);
    }

    public static boolean hideAccidents() {
        return !preferences.getBoolean(showAcc, DEFAULT_SHOW_TYPE);
    }

    public static boolean hideSteals() {
        return !preferences.getBoolean(showSteal, DEFAULT_SHOW_TYPE);
    }

    public static boolean hideOthers() {
        return !preferences.getBoolean(showOther, DEFAULT_SHOW_TYPE);
    }

    public static int getMaxNotifications() {
        return Integer.parseInt(preferences.getString(maxNotifications, String.valueOf(DEFAULT_MAX_NOTIFICATIONS)));
    }

    //TODO Переделать на хранение в локальной БД
    public static JSONArray getNotificationList() {
        JSONArray json;
        try {
            json = new JSONArray(preferences.getString(notificationList, "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONArray();
        }
        return json;
    }

    public static void setNotificationList(JSONArray json) {
        preferences.edit().putString(notificationList, json.toString()).commit();
    }

    public static int getHoursAgo() {
        return Integer.parseInt(preferences.getString(hoursAgo, String.valueOf(DEFAULT_MAX_AGE)));
    }

    public static boolean getVibration() {
        return preferences.getBoolean(useVibration, DEFAULT_VIBRATION);
    }

    public static String getUserName() {
        return preferences.getString(userName, "");
    }

    public static void setUserName(String name) {
        preferences.edit().putString(userName, name).commit();
    }

    public static int getUserId() {
        return preferences.getInt(userId, 0);
    }

    public static void setUserId(int id) {
        preferences.edit().putInt(userId, id).commit();
    }

    public static void setUserRole(String role) {
        preferences.edit().putString(userRole, role).commit();
    }

    public static boolean isNewVersion() {
        return newVersion;
    }

    public static void setNewVersion() {
        Preferences.newVersion = true;
    }

    public static void resetNewVersion() {
        Preferences.newVersion = false;
    }
}
