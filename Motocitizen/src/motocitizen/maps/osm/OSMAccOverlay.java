package motocitizen.maps.osm;

import android.content.Context;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import motocitizen.app.mc.MCAccTypes;
import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;

class OSMAccOverlay {
    public static ItemizedIconOverlay<OverlayItem> getOverlay(Context context) {
        ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem acc;
        for (int id : MCAccidents.points.keySet()) {
            MCPoint p = MCAccidents.points.getPoint(id);
            if (p.getLocation() != null) {
                acc = new OverlayItem(p.getTypeText(), p.getAddress(), new GeoPoint(p.getLocation()));
                acc.setMarker(MCAccTypes.getDrawable(context, p.getType()));
                items.add(acc);
            }
        }
        return new ItemizedIconOverlay<>(context, items, null);
    }
}
