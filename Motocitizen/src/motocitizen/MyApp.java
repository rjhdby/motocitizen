package motocitizen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;

import motocitizen.app.general.user.Auth;

public class MyApp extends Application {

    private static MyApp    instance;
    public static  Auth     auth;
    private static Geocoder geocoder;
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

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static Auth getAuth() {
        if (auth == null) auth = new Auth();
        return auth;
    }

    public static String getAddress(Location location) {
        if (geocoder == null) {
            geocoder = new Geocoder(currentActivity);
        }
        StringBuilder res = new StringBuilder();
        try {
            List<Address> list;
            list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (list == null || list.size() == 0)
                return location.getLatitude() + " " + location.getLongitude();
            Address address = list.get(0);
            String locality = address.getLocality();
            if (locality == null) locality = address.getAdminArea();
            if (locality == null && address.getMaxAddressLineIndex() > 0)
                locality = address.getAddressLine(0);

            String thoroughfare = address.getThoroughfare();
            if (thoroughfare == null) thoroughfare = address.getSubAdminArea();

            String featureName = address.getFeatureName();

            if (locality != null) res.append(locality);
            if (thoroughfare != null) {
                if (res.length() > 0) res.append(" ");
                res.append(thoroughfare);
            }
            if (featureName != null) if (res.length() > 0) res.append(" ");
            res.append(featureName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
