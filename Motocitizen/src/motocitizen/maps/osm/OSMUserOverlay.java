package motocitizen.maps.osm;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import motocitizen.app.mc.MCLocation;
import motocitizen.main.R;
import motocitizen.startup.Startup;

public class OSMUserOverlay {
    public static ItemizedIconOverlay<OverlayItem> getUserOverlay() {
        ArrayList<OverlayItem> items;
        OverlayItem user;
        user = new OverlayItem("Ваша позиция", "Ваша позиция", new GeoPoint(MCLocation.current));
        user.setMarker(Startup.context.getResources().getDrawable(R.drawable.osm_moto_icon));
        items = new ArrayList<>();
        items.add(user);
        return new ItemizedIconOverlay<>(Startup.context, items, null);
    }
}
