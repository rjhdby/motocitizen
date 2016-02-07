package motocitizen.maps.google;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

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

import motocitizen.MyApp;
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

    public MyGoogleMapManager(FragmentActivity activity) {
        jumpToPoint(MyLocationManager.getInstance().getLocation());
        selected = "";
        //parent.removeAllViews();
        //inflate(parent);
        /*
        final SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = ((ActionBarActivity) MyApp.getCurrentActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(parent, (Fragment) mapFragment);
        fragmentTransaction.commit();
        */
        //android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) MyApp.getCurrentActivity()).getSupportFragmentManager();
        android.support.v4.app.FragmentManager     fragmentManager     = activity.getSupportFragmentManager();
        final SupportMapFragment                   mapFragment         = new SupportMapFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.google_map, mapFragment, "MAP").commit();
        //= (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);

        new AsyncTask<Map<String, Integer>, Integer, Integer>() {
            @SafeVarargs
            @Override
            protected final Integer doInBackground(Map<String, Integer>... params) {
                for (int i = 0; i < 5; i++) {
                    map = mapFragment.getMap();
                    if (map != null) break;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return map == null ? 0 : 1;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (integer == 0) return;
                delayedAction.makeAction();
                init();
                placeUser();
            }
        }.execute(null, null, null);
    }

    public void placeUser() {
        if (user != null) {
            user.remove();
        }

        Location location = MyLocationManager.getInstance().getLocation();
        //if(location != null) {
        user = map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(location)).title(Type.USER.toString()).icon(Type.USER.getIcon()));
        //} else {
        //TODO Отобразить сообщение?
        //Toast.makeText(this, MyApp.getCurrentActivity().getString(R.string.position_not_available), Toast.LENGTH_LONG).show();
        //}
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

    public void placeAccidents() {
        if (map == null) return;
        init();
        accidents.clear();
        for (int id : Content.getInstance().keySet()) {
            Accident point = Content.getInstance().get(id);
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

    private void init() {
        map.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();
                if (selected.equals(id) && accidents.containsKey(id)) {
                    MyApp.toDetails(accidents.get(selected));
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
                MyApp.getCurrentActivity().startActivity(intent);
            }
        });
    }

    public void zoom(int zoom) {
        if (map == null) return;
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }


    private class DelayedAnimateToLocation implements DelayedAction {
        Location location;

        public DelayedAnimateToLocation(Location location) {
            this.location = location;
        }

        @Override
        public void makeAction() {
            animateToPoint(location);
        }
    }

    private class DelayedJumpToLocation implements DelayedAction {
        Location location;

        public DelayedJumpToLocation(Location location) {
            this.location = location;
        }

        @Override
        public void makeAction() {
            jumpToPoint(location);
        }
    }
}
