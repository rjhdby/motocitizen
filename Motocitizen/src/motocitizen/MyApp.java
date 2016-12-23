package motocitizen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.app.general.user.Auth;
import motocitizen.content.Content;
import motocitizen.utils.Preferences;

public class MyApp extends MultiDexApplication {

    public static final int LOCATION_PERMISSION = 1;
    public static final int NETWORK_PERMISSION  = 2;

    private static MyApp    instance;
    public static  Geocoder geocoder;
    private static Activity currentActivity;

    static {
        currentActivity = null;
    }

    {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Preferences(this);
        Preferences.setDoNotDisturb(false);
        geocoder = new Geocoder(this);
//        new GCMRegistration();
        Content.getInstance();
//        new GCMBroadcastReceiver();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static void logoff() {
        Auth.getInstance().logoff();
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
}
