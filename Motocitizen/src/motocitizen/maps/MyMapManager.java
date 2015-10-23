package motocitizen.maps;

import android.location.Location;
import android.view.ViewGroup;

public interface MyMapManager {

    public void placeUser();

    public void animateToPoint(Location location);

    public void jumpToPoint(Location location);

    public void zoom(int zoom);

    public void placeAccidents();
}

