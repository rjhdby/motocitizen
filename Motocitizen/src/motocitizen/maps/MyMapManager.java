package motocitizen.maps;

import android.content.Context;
import android.location.Location;

public interface MyMapManager {

    void animateToPoint(Location location);

    void jumpToPoint(Location location);

    void zoom(int zoom);

    void placeAccidents(Context context);

    void enableLocation();
}

