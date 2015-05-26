package motocitizen.app.general;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.MyApp;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.network.requests.GeocodeRequest;
import motocitizen.network.requests.InplaceRequest;
import motocitizen.network.requests.LeaveRequest;
import motocitizen.startup.MyPreferences;
import motocitizen.startup.Startup;

public class MyLocationManager {
    public static String address;
    private static Location current;
    private static MyPreferences prefs;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static Context context;
    private static final com.google.android.gms.location.LocationListener FusionLocationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            current = location;
            prefs.saveLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            requestAddress(context);
            checkInPlace(context, location);
        }
    };
    private static final GoogleApiClient.ConnectionCallbacks connectionCallback = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle connectionHint) {

            while (!mGoogleApiClient.isConnected()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusionLocationListener);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, FusionLocationListener);
            current = getBestFusionLocation(context);
        }

        @Override
        public void onConnectionSuspended(int arg0) {

        }
    };

    public MyLocationManager(Context context) {
        MyLocationManager.context = context;
        prefs = ((MyApp) context.getApplicationContext()).getPreferences();
        mLocationRequest = getProvider(LocationRequest.PRIORITY_HIGH_ACCURACY);
        current = getBestFusionLocation(context);
    }

    private static LocationRequest getProvider(int accuracy) {
        int interval, bestInterval, displacement;
        switch (accuracy) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                interval = 5000;
                bestInterval = 1000;
                displacement = 10;
                break;
            case LocationRequest.PRIORITY_LOW_POWER:
                interval = 60000;
                bestInterval = 30000;
                displacement = 200;
                break;
            default:
                interval = 60000;
                bestInterval = 30000;
                displacement = 200;
        }
        LocationRequest lr = new LocationRequest();
        lr.setInterval(interval);
        lr.setFastestInterval(bestInterval);
        lr.setSmallestDisplacement(displacement);
        lr.setPriority(accuracy);
        return lr;
    }

    /*
     *  Никогда не возвратит null
     */
    public static Location getBestFusionLocation(Context context) {
        Location last = null;
        if (mGoogleApiClient != null) {
            last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (last == null) {
            last = new Location(LocationManager.NETWORK_PROVIDER);
            if (prefs == null) {
                prefs = new MyPreferences(context);
            }
            LatLng latlng = prefs.getSavedLatLng();
            last.setLatitude(latlng.latitude);
            last.setLongitude(latlng.longitude);
            last.setAccuracy(1000);
        }
        return last;
    }

    private static void runLocationService(int accuracy) {
        mLocationRequest = getProvider(accuracy);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallback).addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusionLocationListener);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, FusionLocationListener);
        } else {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public static void sleep() {
        runLocationService(LocationRequest.PRIORITY_LOW_POWER);
    }

    public static void wakeup() {
        runLocationService(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private static void requestAddress(Context context) {
            Location location = getBestFusionLocation(context);
            if (current == location) {
                return;
            }
            new GeocodeRequest(new GeocodeCallback(), location, context);
    }

    private static void checkInPlace(Context context, Location location) {
        String login = prefs.getLogin();
        if (login.equals("")) {
            return;
        }
        int currentInplace = AccidentsGeneral.getInplaceID();
        if (currentInplace != 0) {
            if (isInPlace(location, currentInplace)) {
                return;
            } else {
                AccidentsGeneral.setLeave(currentInplace);
                new LeaveRequest(context, currentInplace);
            }
        }
        for (int accId : AccidentsGeneral.points.keySet()) {
            if (isArrived(location, accId)) {
                AccidentsGeneral.setInPlace(accId);
                new InplaceRequest(context, accId);
            }
        }
    }

    private static boolean isArrived(Location location, int accId) {
        double meters = AccidentsGeneral.points.getPoint(accId).getLocation().distanceTo(location);
        double limit = Math.max(300, location.getAccuracy());
        return meters < limit;
    }

    private static boolean isInPlace(Location location, int accId) {
        Accident acc = AccidentsGeneral.points.getPoint(accId);
        if(acc == null) {
            Toast.makeText(context, "Invalid accident", Toast.LENGTH_LONG).show();
            return false;
        }
        if(location == null ) {
            Toast.makeText(context, "Invalid location", Toast.LENGTH_LONG).show();
            return false;
        }
        if(acc.getLocation() == null ) {
            Toast.makeText(context, "Invalid accident location", Toast.LENGTH_LONG).show();
            return false;
        }

        double meters = AccidentsGeneral.points.getPoint(accId).getLocation().distanceTo(location);
        double limit = location.getAccuracy() * 2 + 1000;
        return meters < limit;
    }

    public static Location getLocation(Context context) {
        return getBestFusionLocation(context);
    }
    private static class GeocodeCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            try {
                address = result.getString("address");
                Startup.updateStatusBar(MyLocationManager.address);
            } catch (JSONException e) {
                address = "Ошибка геокодирования";
                Startup.updateStatusBar(MyLocationManager.address);
                e.printStackTrace();
            }
        }
    }
}
