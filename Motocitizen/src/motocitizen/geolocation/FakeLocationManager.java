package motocitizen.geolocation;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.utils.Preferences;

public class FakeLocationManager implements SecuredLocationManagerInterface {
    private static final int DEFAULT_ACCURACY = 1000;

    @Override
    public void wakeup() {

    }

    @Override
    public void sleep() {

    }

    @Override
    public Location getDirtyLocation() {
        return getLocation();
    }

    @Override
    public Location getLocation() {
        Location current = new Location(LocationManager.NETWORK_PROVIDER);
        LatLng   latlng  = Preferences.getSavedLatLng();
        current.setLatitude(latlng.latitude);
        current.setLongitude(latlng.longitude);
        current.setAccuracy(DEFAULT_ACCURACY);
        return current;
    }

    @Override
    public String getAddress(Location location) {
        return "";
    }
}
