package motocitizen.geolocation;

import android.content.Context;
import android.location.Location;

public interface SecuredLocationManagerInterface {
    void wakeup(Context context);

    void sleep(Context context);

    Location getDirtyLocation();

    Location getLocation();

    String getAddress(Location location);
}
