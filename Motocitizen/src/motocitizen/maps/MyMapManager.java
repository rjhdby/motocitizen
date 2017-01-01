package motocitizen.maps;

import android.content.Context;
import android.location.Location;
import android.view.ViewGroup;

public interface MyMapManager {

    void placeUser();

    void animateToPoint(Location location);

    void jumpToPoint(Location location);

    void zoom(int zoom);

    void placeAccidents(Context context);
}

