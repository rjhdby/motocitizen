package motocitizen.geolocation;

import android.location.Location;

public interface SecuredLocationManagerInterface {
    void wakeup();

    void sleep();

    Location getDirtyLocation();

    Location getLocation();

    String getAddress(Location location);
}
