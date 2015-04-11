package motocitizen.startup;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

import motocitizen.utils.Const;

public class MCPreferences {
    private final static String showAcc = "mc.show.acc";
    private final static String showBreak = "mc.show.break";
    private final static String showSteal = "mc.show.steal";
    private final static String showOther = "mc.show.other";
    private final static String distanceShow = "mc.distance.show";
    private final static String distanceAlarm = "mc.distance.alarm";
    private final static String soundTitle = "mc.notification.sound.title";
    private final static String soundURI = "mc.notification.sound";
    private final static String mapProvider = "mc.map.provider";
    private final static String login = "mc.login";
    private final static String password = "mc.password";
    private final static String anonim = "mc.anonim";
    private final static String GCMRegistrationCode = "mc.gcm.id";
    private final static String appVersion = "mc.app.version";
    private final static String savedlng = "savedlng";
    private final static String savedlat = "savedlat";

    private final static String[] mapProviders = {"google", "osm", "yandex"};

    private static SharedPreferences preferences;
    private static Context context;

    public MCPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public LatLng getSavedLatLng() {
        double lat = (double) preferences.getFloat(savedlat, 55.752295f);
        double lng = (double) preferences.getFloat(savedlng, 37.622735f);
        return new LatLng(lat, lng);
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
        }catch (Exception e){
            String distanceString = preferences.getString(distanceShow, "200");
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public int getAlarmDistance() {
        int distance;
        try {
            distance = preferences.getInt(distanceAlarm, 20);
        }catch (Exception e){
            String distanceString = preferences.getString(distanceAlarm, "20");
            distance = Integer.parseInt(distanceString);
        }
        return distance;
    }

    public String getAlarmSoundTitle() {
        return preferences.getString(soundTitle, "default system");
    }

    public Uri getAlarmSoundUri() {
        Uri uri;
        String uriString = preferences.getString(soundURI, "default");
        if (uriString.equals("default")) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(this.context, RingtoneManager.TYPE_NOTIFICATION);
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
        preferences.edit().putInt(appVersion, code);
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
        preferences.edit().putInt(distanceShow, distance).commit();
    }

    public void setAlarmDistance(int distance) {
        if (distance > Const.EQUATOR) {
            distance = Const.EQUATOR;
        }
        preferences.edit().putInt(distanceAlarm, distance).commit();
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
        if (globalType.equals("acc_m")) {
            return toShowAcc();
        } else if (globalType.equals("acc_b")) {
            return toShowBreak();
        } else if (globalType.equals("acc_s")) {
            return toShowSteal();
        } else {
            return toShowOther();
        }
    }

    public void resetPreferences(){
        preferences.edit().clear();
    }
}
