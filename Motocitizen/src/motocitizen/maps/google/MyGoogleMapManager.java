package motocitizen.maps.google;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.accident.Accident;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.content.Type;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.maps.MyMapManager;
import motocitizen.utils.MyUtils;

@SuppressLint("UseSparseArrays")
public class MyGoogleMapManager extends MyMapManager {
    private static GoogleMap map;
    private static Marker    user;
    private static String    selected;
    private static Map<String, Integer> accidents = new HashMap<>();

    public MyGoogleMapManager(final Context context) {
        setName(MyMapManager.GOOGLE);
        selected = "";

        LayoutInflater li     = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup      parent = (ViewGroup) ((Activity) context).findViewById(R.id.map_container);
        View           child  = li.inflate(R.layout.google_maps_view, parent, false);
        if (parent.getChildCount() > 0) parent.removeAllViews();
        parent.addView(child);

        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        final SupportMapFragment               mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);

//TODO Это пиздец
        for (int i = 0; i < 5; i++) {
            map = mapFragment.getMap();
            if (map == null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        init();
        map.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();
                if (selected.equals(id) && accidents.containsKey(id)) {
                    Content.toDetails(context, accidents.get(selected));
                } else {
                    marker.showInfoWindow();
                    selected = id;
                }
                return true;
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String uri    = "geo:" + latLng.latitude + "," + latLng.longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });
    }

    @SuppressWarnings("UnusedParameters")
    public void placeUser(Context context) {
        if (user != null) {
            user.remove();
        }

        Location location = MyLocationManager.getLocation();
        //if(location != null) {
        user = map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(location)).title(Type.USER.toString()).icon(Type.USER.getIcon()));
        //} else {
        //TODO Отобразить сообщение?
        //Toast.makeText(this, Startup.context.getString(R.string.position_not_available), Toast.LENGTH_LONG).show();
        //}
    }

    public void jumpToPoint(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), 16));
    }

    @SuppressWarnings("UnusedParameters")
    public void placeAccidents(Context context) {
        init();
        accidents.clear();
        for (int id : Content.getPoints().keySet()) {
            Accident point = Content.get(id);
            if (point.isInvisible()) continue;
            String title = point.getType().toString();
            title += point.getMedicine() != Medicine.UNKNOWN ? ", " + point.getMedicine().toString() : "";
            title += ", " + MyUtils.getIntervalFromNowInText(point.getTime()) + " назад";

            float alpha;
            int age = (int) (((new Date()).getTime() - point.getTime().getTime()) / 3600000);
            if (age < 2) {
                alpha = 1.0f;
            } else if (age < 6) {
                alpha = 0.5f;
            } else {
                alpha = 0.2f;
            }
            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())).title(title).icon(point.getType().getIcon()).alpha(alpha));
            accidents.put(marker.getId(), id);
        }
    }

    private static void init() {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void zoom(int zoom) {
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }
}
