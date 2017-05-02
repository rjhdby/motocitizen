package motocitizen.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.utils.Preferences;

class FakeLocationManager implements SecuredLocationManagerInterface {
    private static final int DEFAULT_ACCURACY = 1000;

    @Override
    public void wakeup(Context context) {

    }

    @Override
    public void sleep(Context context) {

    }

    @Override
    public Location getDirtyLocation() {
        return getLocation();
    }

    @Override
    public Location getLocation() {
        Location current = new Location(LocationManager.NETWORK_PROVIDER);
        LatLng   latlng  = Preferences.getInstance().getSavedLatLng();
        current.setLatitude(latlng.latitude);
        current.setLongitude(latlng.longitude);
        current.setAccuracy(DEFAULT_ACCURACY);
        return current;
    }

    @Override
    public String getAddress(LatLng location) {
        return "";
    }
}
