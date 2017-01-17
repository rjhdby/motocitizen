package motocitizen.maps.google;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.activity_old.AccidentDetailsActivity;
import motocitizen.accident.Accident;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.content.Type;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.maps.MyMapManager;
import motocitizen.utils.DelayedAction;
import motocitizen.utils.MyUtils;

public class MyGoogleMapManager implements MyMapManager {
    private static final int DEFAULT_ZOOM = 16;

    private GoogleMap map;
    private Marker    user;
    private String    selected;
    private Map<String, Integer> accidents = new HashMap<>();

    private DelayedAction delayedAction;

    private class OnMapCreated implements OnMapReadyCallback {
        private Context context;

        OnMapCreated(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            delayedAction.makeAction();
            init(context);
            placeUser();
        }
    }

    public MyGoogleMapManager(FragmentActivity activity) {
        jumpToPoint(MyLocationManager.getInstance().getLocation());
        selected = "";
        android.support.v4.app.FragmentManager     fragmentManager     = activity.getSupportFragmentManager();
        final SupportMapFragment                   mapFragment         = new SupportMapFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.google_map, mapFragment, "MAP").commit();

        mapFragment.getMapAsync(new OnMapCreated(activity));

    }

    public void placeUser() {
        if (user != null) {
            user.remove();
        }

        Location location = MyLocationManager.getInstance().getLocation();
        user = map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(location)).title(Type.USER.toString()).icon(Type.USER.getIcon()));
        //TODO Отобразить сообщение?
    }

    public void animateToPoint(Location location) {
        if (map == null) delayedAction = new DelayedAnimateToLocation(location);
        else
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), DEFAULT_ZOOM));
    }

    public void jumpToPoint(Location location) {
        if (map == null) delayedAction = new DelayedJumpToLocation(location);
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(location), DEFAULT_ZOOM));
    }

    public void placeAccidents(Context context) {
        if (map == null) return;
        init(context);
        accidents.clear();
        for (int id : Content.getInstance().keySet()) {
            Accident point = Content.getInstance().get(id);
            if (point.isInvisible()) continue;
            String title = point.getType().toString();
            title += point.getMedicine() != Medicine.UNKNOWN ? ", " + point.getMedicine().toString() : "";
            title += ", " + MyUtils.getIntervalFromNowInText(context, point.getTime()) + " назад";

            int age = (int) (((new Date()).getTime() - point.getTime().getTime()) / 3600000);

            float alpha = age < 2 ? 1.0f : age < 6 ? 0.5f : 0.2f;

            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())).title(title).icon(point.getType().getIcon()).alpha(alpha));
            accidents.put(marker.getId(), id);
        }
    }

    private void init(final Context context) {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(marker -> {
            String id = marker.getId();
            if (selected.equals(id) && accidents.containsKey(id)) {
                toDetails(context, accidents.get(selected));
            } else {
                marker.showInfoWindow();
                selected = id;
            }
            return true;
        });
        map.setOnMapLongClickListener(latLng -> {
            String uri    = "geo:" + latLng.latitude + "," + latLng.longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });
    }

    private void toDetails(Context context, int id) {
        Intent intent = new Intent(context, AccidentDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("accidentID", id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void zoom(int zoom) {
        if (map == null) return;
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }


    private class DelayedAnimateToLocation implements DelayedAction {
        Location location;

        DelayedAnimateToLocation(Location location) {
            this.location = location;
        }

        @Override
        public void makeAction() {
            animateToPoint(location);
        }
    }

    private class DelayedJumpToLocation implements DelayedAction {
        Location location;

        DelayedJumpToLocation(Location location) {
            this.location = location;
        }

        @Override
        public void makeAction() {
            jumpToPoint(location);
        }
    }
}
