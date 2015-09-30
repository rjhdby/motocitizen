package motocitizen.maps;

import android.content.Context;
import android.location.Location;

public abstract class MyMapManager {

    public static final String OSM    = "osm";
    public static final String GOOGLE = "google";
    public static final String YANDEX = "yandex";

    private String name;

    public abstract void placeUser();

    public abstract void jumpToPoint(Location location);

    @SuppressWarnings("SameParameterValue")
    public abstract void zoom(int zoom);

    public abstract void placeAccidents();

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }
}

