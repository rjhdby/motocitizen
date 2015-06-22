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

import motocitizen.main.R;
import motocitizen.utils.Const;

@SuppressLint("CommitPrefEdits")
public class MyPreferences {
    public final String showAcc        = "mc.show.acc";
    public final String showBreak      = "mc.show.break";
    public final String showSteal      = "mc.show.steal";
    public final String showOther      = "mc.show.other";
    public final String distanceShow   = "mc.distance.show";
    public final String distanceAlarm  = "mc.distance.alarm";
    public final String mapProvider    = "mc.map.provider";
    public final String currentVersion = "version";
    public final String doNotDisturb   = "do.not.disturb";

    private final static String onWay               = "mc.onway";
    private final static String soundTitle          = "mc.notification.sound.title";
    private final static String soundURI            = "mc.notification.sound";
    private final static String login               = "mc.login";
    private final static String password            = "mc.password";
    private final static String anonim              = "mc.anonim";
    private final static String GCMRegistrationCode = "mc.gcm.id";
    private final static String appVersion          = "mc.app.version";
    private final static String savedlng            = "savedlng";
    private final static String savedlat            = "savedlat";

    private final static String notificationList = "notificationList";
    private final static String maxNotifications = "notifications.max";

    private final static String[] mapProviders = {"google", "osm", "yandex"};

    private static SharedPreferences preferences;
    private static Context           context;

    public MyPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        MyPreferences.context = context;
    }

    public void putBoolean(String name, boolean value) {
        preferences.edit().putBoolean(name, value).commit();
    }

    public static void setOnWay(int id) {
        preferences.edit().putInt(onWay, id).commit();
    }

    public static int getOnWay() {
        return preferences.getInt(onWay, 0);
    }

    public LatLng getSavedLatLng() {
        double lat = (double) preferences.getFloat(savedlat, 55.752295f);
        double lng = (double) preferences.getFloat(savedlng, 37.622735f);
        return new LatLng(lat, lng);
    }

    public void setDoNotDisturb(boolean value) {
        preferences.edit().putBoolean(doNotDisturb, value).commit();
    }

    public boolean getDoNotDisturb() {
        return preferences.getBoolean(doNotDisturb, false);
    }

    public String getCurrentVersion() {
        return preferences.getString(currentVersion, context.getString(R.string.unknown_code_version));
    }

    public void setCurrentVersion(String version) {
        preferences.edit().putString(currentVersion, version).commit();
    }

    public void saveLatLng(LatLng latlng) {
        preferences.edit().putFloat(savedlat, (float) latlng.latitude)
                   .putFloat(savedlng, (float) latlng.longitude).commit();
    }

    public boolean toShowAcc() {
        return preferences.getBoolean(showAcc, true);
    }

    public boolean toShowSteal() {
        return preferences.getBoolean(showSteal, true);
    }

    public boolean toShowBreak() {
        return preferences.getBoolean(showBreak, true);
    }

    public boolean toShowOther() {
        return preferences.getBoolean(showOther, true);
    }

    public int getVisibleDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceShow, 200);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceShow, "200");
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public int getAlarmDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceAlarm, 20);
        } catch (Exception e) {
            String distanceString = preferences.getString(distanceAlarm, "20");
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public String getAlarmSoundTitle() {
        return preferences.getString(soundTitle, "default system");
    }

    public Uri getAlarmSoundUri() {
        Uri    uri;
        String uriString = preferences.getString(soundURI, "default");
        if (uriString.equals("default")) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        } else
            uri = Uri.parse(uriString);
        return uri;
    }

    public String getMapProvider() {
        return preferences.getString(mapProvider, "google");
    }

    public String getLogin() {
        return preferences.getString(login, "");
    }

    public String getPassword() {
        return preferences.getString(password, "");
    }

    public boolean isAnonim() {
        return preferences.getBoolean(anonim, false);
    }

    public String getGCMRegistrationCode() {
        return preferences.getString(GCMRegistrationCode, "");
    }

    public void setGCMRegistrationCode(String code) {
        preferences.edit().putString(GCMRegistrationCode, code).commit();
    }

    public int getAppVersion() {
        return preferences.getInt(appVersion, 0);
    }

    public void setAppVersion(int code) {
        preferences.edit().putInt(appVersion, code).commit();
    }

    public void setShowAcc(boolean show) {
        preferences.edit().putBoolean(showAcc, show).commit();
    }

    public void setShowBreak(boolean show) {
        preferences.edit().putBoolean(showBreak, show).commit();
    }

    public void setShowSteal(boolean show) {
        preferences.edit().putBoolean(showSteal, show).commit();
    }

    public void setShowOther(boolean show) {
        preferences.edit().putBoolean(showOther, show).commit();
    }

    public void setAnonim(boolean value) {
        preferences.edit().putBoolean(anonim, value).commit();
    }

    public void setMapProvider(String provider) {
        if (Arrays.asList(mapProviders).contains(provider)) {
            preferences.edit().putString(mapProvider, provider);
        }
    }

    public void setSoundAlarm(String title, Uri uri) {
        preferences.edit().putString(soundTitle, title).putString(soundURI, uri.toString()).commit();
    }

    public void setDefaultSoundAlarm() {
        preferences.edit().putString(soundTitle, "default system").putString(soundURI, "default").commit();
    }

    public void setVisibleDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putString(distanceShow, String.valueOf(distance)).commit();
    }

    public void setAlarmDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putString(distanceAlarm, String.valueOf(distance)).commit();
    }

    public void setLogin(String value) {
        preferences.edit().putString(login, value).commit();
    }

    public void setPassword(String value) {
        preferences.edit().putString(password, value).commit();
    }

    public void resetAuth() {
        preferences.edit().remove(login).remove(password).commit();
    }

    public boolean toShowAccType(String type) {
        String globalType = type.substring(0, 5);
        switch (globalType) {
            case "acc_m":
                return toShowAcc();
            case "acc_b":
                return toShowBreak();
            case "acc_s":
                return toShowSteal();
            default:
                return toShowOther();
        }
    }

    public void resetPreferences() {
        preferences.edit().clear();
    }

    public int getMaxNotifications() {
        return preferences.getInt(maxNotifications, 3);
    }

    public void setMaxNotifications(int code) {
        preferences.edit().putInt(maxNotifications, code).commit();
    }

    public String getAccidentTypeName(String type) {
        switch (type) {
            case "acc_s":
                return "Угон";
            case "acc_b":
                return "Поломка";
            case "acc_m":
                return "ДТП, один участник";
            case "acc_m_m":
                return "ДТП, мот/мот";
            case "acc_m_a":
                return "ДТП, мот/авто";
            case "acc_m_p":
                return "Наезд на пешехода";
            case "acc_o":
                return "Прочее";
            default:
                return "";
        }
    }

    public String getMedTypeName(String type) {
        switch (type) {
            case "mc_m_na":
                return "";
            case "mc_m_wo":
                return "Без повреждений";
            case "mc_m_l":
                return "Ушибы";
            case "mc_m_h":
                return "Тяжелые травмы";
            case "mc_m_d":
                return "Минус";
            default:
                return "";
        }
    }

    public static void setNotificationList(JSONArray json) {
        preferences.edit().putString(notificationList, json.toString());
    }

    public static JSONArray getNotificationList() {
        JSONArray json;
        try {
            json = new JSONArray(preferences.getString(notificationList, "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONArray();
        }
        return json;
    }
}

