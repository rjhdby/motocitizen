package motocitizen.startup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

import motocitizen.content.Type;
import motocitizen.main.R;
import motocitizen.utils.Const;

@SuppressLint("CommitPrefEdits")
public class Preferences {

    public final static  String showAcc             = "mc.show.acc";
    public final static  String showBreak           = "mc.show.break";
    public final static  String showSteal           = "mc.show.steal";
    public final static  String showOther           = "mc.show.other";
    public final static  String distanceShow        = "mc.distance.show";
    public final static  String distanceAlarm       = "mc.distance.alarm";
    public final static  String mapProvider         = "mc.map.provider";
    public final static  String currentVersion      = "version";
    public final static  String doNotDisturb        = "do.not.disturb";
    public final static  String hoursAgo            = "hours.ago";
    public final static  String maxNotifications    = "notifications.max";
    public final static  String useVibration        = "use.vibration";
    public final static  String userId              = "userId";
    public final static  String userName            = "userName";
    public final static  String userRole            = "userRole";
    private final static String onWay               = "mc.onway";
    private final static String soundTitle          = "mc.notification.sound.title";
    private final static String soundURI            = "mc.notification.sound";
    private final static String login               = "mc.login";
    private final static String password            = "mc.password";
    private final static String anonim              = "mc.anonim";
    private final static String GCMRegistrationCode = "mc.gcm.id";
    private final static String appVersion          = "mc.app.version";
    private final static String savedLng            = "savedlng";
    private final static String savedLat            = "savedlat";
    private final static String notificationList    = "notificationList";
    private final static String messageReadList     = "messageReadList";

    private final static String[] mapProviders = {"google", "osm", "yandex"};

    private static SharedPreferences preferences;
    private static Context           context;

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Preferences.context = context;
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
        double lat = (double) preferences.getFloat(savedLat, 55.752295f);
        double lng = (double) preferences.getFloat(savedLng, 37.622735f);
        return new LatLng(lat, lng);
    }

    public static boolean getDoNotDisturb() {
        return preferences.getBoolean(doNotDisturb, false);
    }

    public static void setDoNotDisturb(boolean value) {
        preferences.edit().putBoolean(doNotDisturb, value).commit();
    }

    public static String getCurrentVersion() {
        return preferences.getString(currentVersion, context.getString(R.string.unknown_code_version));
    }

    public static void setCurrentVersion(String version) {
        preferences.edit().putString(currentVersion, version).commit();
    }

    public static void saveLatLng(LatLng latlng) {
        preferences.edit().putFloat(savedLat, (float) latlng.latitude).putFloat(savedLng, (float) latlng.longitude).commit();
    }

    public static int getVisibleDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceShow, 200);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceShow, "200");
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
            distance = preferences.getInt(distanceAlarm, 20);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceAlarm, "20");
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

    public static Uri getAlarmSoundUri() {
        Uri    uri;
        String uriString = preferences.getString(soundURI, "default");
        if (uriString.equals("default")) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        } else uri = Uri.parse(uriString);
        return uri;
    }

    public static String getMapProvider() {
        return preferences.getString(mapProvider, "google");
    }

    public static void setMapProvider(String provider) {
        if (Arrays.asList(mapProviders).contains(provider)) {
            preferences.edit().putString(mapProvider, provider);
        }
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
        return preferences.getBoolean(anonim, false);
    }

    public static void setAnonim(boolean value) {
        preferences.edit().putBoolean(anonim, value).commit();
    }

    public static String getGCMRegistrationCode() {
        return preferences.getString(GCMRegistrationCode, "");
    }

    public static void setGCMRegistrationCode(String code) {
        preferences.edit().putString(GCMRegistrationCode, code).commit();
    }

    public static int getAppVersion() {
        return preferences.getInt(appVersion, 0);
    }

    public static void setAppVersion(int code) {
        preferences.edit().putInt(appVersion, code).commit();
    }

    public static void setShowAcc(boolean show) {
        preferences.edit().putBoolean(showAcc, show).commit();
    }

    public static void setShowBreak(boolean show) {
        preferences.edit().putBoolean(showBreak, show).commit();
    }

    public static void setShowSteal(boolean show) {
        preferences.edit().putBoolean(showSteal, show).commit();
    }

    public static void setShowOther(boolean show) {
        preferences.edit().putBoolean(showOther, show).commit();
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
                return !toShowBreak();
            case MOTO_AUTO:
            case MOTO_MOTO:
            case MOTO_MAN:
            case SOLO:
                return !toShowAcc();
            case STEAL:
                return !toShowSteal();
            case OTHER:
            default:
                return !toShowOther();
        }
    }

    public static boolean toShowBreak() {
        return preferences.getBoolean(showBreak, true);
    }

    public static boolean toShowAcc() {
        return preferences.getBoolean(showAcc, true);
    }

    public static boolean toShowSteal() {
        return preferences.getBoolean(showSteal, true);
    }

    public static boolean toShowOther() {
        return preferences.getBoolean(showOther, true);
    }

    public static void resetPreferences() {
        preferences.edit().clear();
    }

    public static int getMaxNotifications() {
        return Integer.parseInt(preferences.getString(maxNotifications, "3"));
    }

    public static void setMaxNotifications(int code) {
        preferences.edit().putInt(maxNotifications, code).commit();
    }

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

    public static JSONArray getMessageReadList() {
        JSONArray json;
        try {
            json = new JSONArray(preferences.getString(messageReadList, "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONArray();
        }
        return json;
    }

    public static void setMessageReadList(JSONArray json) {
        preferences.edit().putString(messageReadList, json.toString()).commit();
    }

    public static int getHoursAgo() {
        return Integer.parseInt(preferences.getString(hoursAgo, "24"));
    }

    public static void setHoursAgo(int hours) {
        preferences.edit().putString(hoursAgo, String.valueOf(hours)).commit();
    }

    public static boolean getVibration() {
        return preferences.getBoolean(useVibration, true);
    }

    public static void setVibration(boolean vibration) {
        preferences.edit().putString(useVibration, String.valueOf(vibration)).commit();
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

    public static String getUserRole() {
        return preferences.getString(userRole, "readonly");
    }

    public static void setUserRole(String role) {
        preferences.edit().putString(userRole, role).commit();
    }
}
