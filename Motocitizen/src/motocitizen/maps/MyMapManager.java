package motocitizen.maps;

import android.location.Location;

public abstract class MyMapManager {

    protected static final String OSM    = "osm";
    protected static final String GOOGLE = "google";
    protected static final String YANDEX = "yandex";

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

