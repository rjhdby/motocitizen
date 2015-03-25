package motocitizen.maps.google;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccTypes;
import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCLocation;
import motocitizen.app.mc.MCPoint;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Inflate;
import motocitizen.utils.MCUtils;

@SuppressLint("UseSparseArrays")
public class MCGoogleMap {
    private static GoogleMap map;
    private static Marker user;
    private static Map<String, Integer> accidents;
    private static String selected;

    public MCGoogleMap(Context context) {
        selected = "";
        Inflate.add(R.id.map_container, R.layout.google_maps_view);
        map = ((MapFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.google_map)).getMap();
        init();
        map.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();
                if (selected.equals(id) && accidents.containsKey(id)) {
                    MCAccidents.toDetails(Startup.context, accidents.get(selected));
                } else {
                    marker.showInfoWindow();
                    selected = id;
                }
                return true;
            }
        });
    }

    @SuppressWarnings("UnusedParameters")
    public static void placeUser(Context context) {
        if (user != null) {
            user.remove();
        }
        user = map.addMarker(new MarkerOptions().position(MCUtils.LocationToLatLng(MCLocation.current)).title("Вы")
                .icon(MCAccTypes.getBitmapDescriptor("user")));
    }

    public static void jumpToPoint(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MCUtils.LocationToLatLng(location), 16));
    }

    @SuppressWarnings("UnusedParameters")
    public static void placeAcc(Context context) {
        if (accidents == null) {
            accidents = new HashMap<>();
        }
        init();
        accidents.clear();
        for (int id : MCAccidents.points.keySet()) {
            MCPoint p = MCAccidents.points.getPoint(id);
            String title = p.getTypeText();
            if (!p.getMedText().equals("")) {
                title += ", " + p.getMedText();
            }
            title += ", " + MCUtils.getIntervalFromNowInText(p.created) + " назад";

            float alpha;
            int age = (int) (((new Date()).getTime() - p.created.getTime()) / 3600000);
            if (age < 2) {
                alpha = 1.0f;
            } else if (age < 6) {
                alpha = 0.5f;
            } else {
                alpha = 0.2f;
            }
            Marker marker = map.addMarker(new MarkerOptions().position(MCUtils.LocationToLatLng(p.location)).title(title)
                    .icon(MCAccTypes.getBitmapDescriptor(p.type)).alpha(alpha));
            accidents.put(marker.getId(), id);
        }
    }

    private static void init() {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public static void zoom(int zoom) {
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }
}
