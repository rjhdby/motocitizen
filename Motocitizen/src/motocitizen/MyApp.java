package motocitizen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import motocitizen.app.general.user.Auth;
import motocitizen.app.general.user.Role;
import motocitizen.content.Content;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.maps.MyMapManager;
import motocitizen.utils.Preferences;

public class MyApp extends Application {

    private static MyApp             instance;
    private static Auth              auth;
    private static Geocoder          geocoder;
    private static Activity          currentActivity;
    private static MyMapManager      map;
    private static MyLocationManager locationManager;

    static {
        currentActivity = null;
        map = null;
    }

    {
        instance = this;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        MyApp.currentActivity = currentActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Preferences(this);
        Preferences.setDoNotDisturb(false);
        auth = new Auth();
        geocoder = new Geocoder(this);
        locationManager = new MyLocationManager();
        new GCMRegistration();
        new Content();
        new GCMBroadcastReceiver();
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static MyLocationManager getLocationManager() {
        return locationManager;
    }

    public static Auth getAuth() {
        return auth;
    }

    public static Role getRole() {
        return auth.getRole();
    }

    public static boolean isAuthorized() {
        return auth.isAuthorized();
    }

    public static void logoff() {
        auth.logoff();
    }

    public static MyMapManager getMap() {
        return map;
    }

    public static Geocoder getGeocoder() {
        return geocoder;
    }

    public static void setMap(MyMapManager map) {
        MyApp.map = map;
    }

    public static boolean isOnline() {
        ConnectivityManager cm      = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
