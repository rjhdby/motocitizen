package motocitizen.maps;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public interface MyMapManager {

    void animateToPoint(Location location);

    void jumpToPoint(LatLng latLng);

    void zoom(int zoom);

    void placeAccidents(Context context);

    void enableLocation();
}

