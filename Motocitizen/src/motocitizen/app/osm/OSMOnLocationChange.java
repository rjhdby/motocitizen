package motocitizen.app.osm;

import java.util.ArrayList;

import motocitizen.main.R;
import motocitizen.startup.Startup;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.location.Location;

public class OSMOnLocationChange {
	private static final Activity act = (Activity) Startup.context;
	private static OverlayItem USER = null;
	private static ArrayList<OverlayItem> items;
	private static ItemizedIconOverlay<OverlayItem> IIO = null;
	public static MapView map;

	public OSMOnLocationChange() {
	}

	public static void action(final Location location) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				placeUser(location);
			}
		});
	}

	public static void placeUser(Location location) {
		map = (MapView) act.findViewById(R.id.osm_mapview);
		GeoPoint gp = new GeoPoint(location);
		map.getController().setCenter(gp);

		if (IIO != null) {
			map.getOverlays().remove(IIO);
			items.remove(USER);
		}
		USER = new OverlayItem("1", "2", gp);
		USER.setMarker(act.getResources().getDrawable(R.drawable.osm_moto_icon));
		items = new ArrayList<OverlayItem>();
		items.add(USER);
		IIO = new ItemizedIconOverlay<OverlayItem>(act, items, null);
		map.getOverlays().add(IIO);
		map.invalidate();
	}
}
