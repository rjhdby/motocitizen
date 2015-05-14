package motocitizen.maps.osm;

import android.content.Context;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import motocitizen.app.general.AccidentTypes;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.Accident;

class OSMAccOverlay {
    public static ItemizedIconOverlay<OverlayItem> getOverlay(Context context) {
        ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem acc;
        for (int id : AccidentsGeneral.points.keySet()) {
            Accident p = AccidentsGeneral.points.getPoint(id);
            if (p.getLocation() != null) {
                acc = new OverlayItem(p.getTypeText(), p.getAddress(), new GeoPoint(p.getLocation()));
                acc.setMarker(AccidentTypes.getDrawable(context, p.getType()));
                items.add(acc);
            }
        }
        return new ItemizedIconOverlay<>(context, items, null);
    }
}
