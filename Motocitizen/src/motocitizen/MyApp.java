package motocitizen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.util.List;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.app.general.user.Auth;
import motocitizen.app.general.user.Role;
import motocitizen.content.Content;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.utils.Preferences;

public class MyApp extends Application {

    private static MyApp    instance;
    private static Auth     auth;
    private static Geocoder geocoder;
    private static Activity currentActivity;

    private static MyLocationManager locationManager;
    private static Content           content;

    static {
        currentActivity = null;
        content = null;
    }

    {
        instance = this;
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
        content = new Content();
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

    public static Content getContent() {
        return content;
    }

    public static void setContent(Content content) {
        MyApp.content = content;
    }

    public static boolean isAuthorized() {
        return auth.isAuthorized();
    }

    public static void logoff() {
        auth.logoff();
    }

    public static Geocoder getGeoCoder() {
        return geocoder;
    }

    public static boolean isOnline() {
        ConnectivityManager cm      = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        MyApp.currentActivity = currentActivity;
    }

    public static void toDetails(int id) {
        Intent intent = new Intent(getAppContext(), AccidentDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("accidentID", id);
        intent.putExtras(bundle);
        getCurrentActivity().startActivity(intent);
    }

    public static List<Integer> getFavorites() {
        return content.favorites;
    }
}
