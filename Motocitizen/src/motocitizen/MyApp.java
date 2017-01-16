package motocitizen;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import motocitizen.geocoder.MyGeocoder;
import motocitizen.user.Auth;
import motocitizen.content.Content;
import motocitizen.database.DbOpenHelper;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.utils.Preferences;

public class MyApp extends MultiDexApplication {

    public static final int LOCATION_PERMISSION = 1;
    public static final int NETWORK_PERMISSION  = 2;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void logoff() {
        Auth.getInstance().logoff();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
