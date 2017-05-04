package motocitizen.geolocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

class FakeLocationManager implements SecuredLocationManagerInterface {

    FakeLocationManager() {}

    @Override
    public void wakeup(Context context) {

    }

    @Override
    public void sleep(Context context) {

    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String getAddress(LatLng location) {
        return "";
    }
}
