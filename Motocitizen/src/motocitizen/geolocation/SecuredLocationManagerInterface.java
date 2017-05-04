package motocitizen.geolocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public interface SecuredLocationManagerInterface {
    void wakeup(Context context);

    void sleep(Context context);

    Location getLocation();

    String getAddress(LatLng location);
}
