package motocitizen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Geocoder;
import android.os.StrictMode;

import motocitizen.app.general.user.Auth;
import motocitizen.app.general.user.Role;
import motocitizen.content.Content;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.utils.Preferences;

public class MyApp extends Application {

    private static MyApp    instance;
    public static  Auth     auth;
    public static Geocoder geocoder;
    private static Activity currentActivity;

    static {
        auth = null;
        geocoder = null;
        currentActivity = null;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        MyApp.currentActivity = currentActivity;
    }

    public MyApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Preferences(this);
        auth = new Auth();
        geocoder = new Geocoder(this);
        new MyLocationManager();
        new GCMRegistration();
        new Content();
        new GCMBroadcastReceiver();
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
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
}
