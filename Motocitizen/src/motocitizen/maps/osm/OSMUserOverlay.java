package motocitizen.maps.osm;

import android.content.Context;
import android.location.Location;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import motocitizen.app.mc.MCLocation;
import motocitizen.main.R;

class OSMUserOverlay {
    public static ItemizedIconOverlay<OverlayItem> getUserOverlay(Context context) {
        ArrayList<OverlayItem> items;
        OverlayItem user;

        Location location = MCLocation.getLocation(context);
        //if( location != null) {
            user = new OverlayItem("Ваша позиция", "Ваша позиция", new GeoPoint(location));
            user.setMarker(context.getResources().getDrawable(R.drawable.osm_moto_icon));
            items = new ArrayList<>();
            items.add(user);
            return new ItemizedIconOverlay<>(context, items, null);
        //} else {
            //TODO Отобразить сообщение?
            //Toast.makeText(this, Startup.context.getString(R.string.position_not_available), Toast.LENGTH_LONG).show();
        //    return null;
        //}
    }
}
