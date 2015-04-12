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
            if (p.location != null) {
                acc = new OverlayItem(p.getTypeText(), p.address, new GeoPoint(p.location));
                acc.setMarker(MCAccTypes.getDrawable(context, p.type));
                items.add(acc);
            }
        }
        return new ItemizedIconOverlay<>(context, items, null);
    }
}
