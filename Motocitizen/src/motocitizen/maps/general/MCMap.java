package motocitizen.maps.general;

import motocitizen.maps.google.MCGoogleMap;
import motocitizen.maps.osm.MCOSMMap;
import android.content.Context;
import android.location.Location;

public class MCMap {
	public final static int OSM = 1;
	public final static int GOOGLE = 2;
	public final static int YANDEX = 3;

	public static int CURRENT;

	public MCMap(Context context) {
		CURRENT = GOOGLE;
		switch (CURRENT) {
		case OSM:
			new MCOSMMap(context);
			break;
		case GOOGLE:
			new MCGoogleMap(context);
			break;
		case YANDEX:
		}
	}

	public static void placeUser(Context context) {
		switch (CURRENT) {
		case OSM:
			MCOSMMap.placeUser(context);
			break;
		case GOOGLE:
			MCGoogleMap.placeUser(context);
			break;
		case YANDEX:
		}

	}

	public static void jumpToPoint(Location location) {
		switch (CURRENT) {
		case OSM:
			MCOSMMap.jumpToPoint(location);
			break;
		case GOOGLE:
			MCGoogleMap.jumpToPoint(location);
			break;
		case YANDEX:
		}

	}

	public static void zoom(int zoom) {
		switch (CURRENT) {
		case OSM:
			MCOSMMap.zoom(zoom);
			break;
		case GOOGLE:
			MCGoogleMap.zoom(zoom);
			break;
		case YANDEX:
		}

	}

	public static void placeAcc(Context context) {
		switch (CURRENT) {
		case OSM:
			MCOSMMap.placeAcc(context);
			break;
		case GOOGLE:
			MCGoogleMap.placeAcc(context);
			break;
		case YANDEX:
		}

	}
}
