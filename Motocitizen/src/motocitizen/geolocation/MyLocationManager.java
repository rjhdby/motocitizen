package motocitizen.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.utils.Preferences;

public class MyLocationManager {
    private static final int     DEFAULT_ACCURACY = 1000;
    private SecuredLocationManagerInterface manager;

    private MyLocationManager() {
        manager = new FakeLocationManager();
    }

    public static void enableReal() {
        getInstance().manager = new NormalLocationManager();
    }

    public void wakeup(Context context) {
        manager.wakeup(context);
    }

    public void sleep(Context context) {
        manager.sleep(context);
    }

    public static Location getLocation() {
        return getInstance().getRealLocation();
    }

    private Location getLast() {
        Preferences preferences = Preferences.dirtyRead();
        LatLng      latLng      = preferences == null ? new LatLng(Preferences.DEFAULT_LATITUDE, Preferences.DEFAULT_LONGITUDE) : preferences.getSavedLatLng();
        Location    current     = new Location(LocationManager.NETWORK_PROVIDER);
        current.setLatitude(latLng.latitude);
        current.setLongitude(latLng.longitude);
        current.setAccuracy(DEFAULT_ACCURACY);
        return current;
    }

    private Location getRealLocation() {
        if (manager == null) return getLast();
        Location current = manager.getLocation();
        if (current == null) return getLast();
        return current;
    }

    public String getAddress(LatLng location) {
        return manager.getAddress(location);
    }

    private static class Holder {
        static MyLocationManager instance;
    }


    public static MyLocationManager getInstance() {
        if (Holder.instance == null) {
            Holder.instance = new MyLocationManager();
        }
        return Holder.instance;
    }
}
