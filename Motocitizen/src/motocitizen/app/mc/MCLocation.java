package motocitizen.app.mc;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.GeoCodeRequest;
import motocitizen.network.JsonRequest;
import motocitizen.startup.MCPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

public class MCLocation {
    private static final String TAG = "LOCATION";
    public static Location current;
    private static MCPreferences prefs;
    private static final com.google.android.gms.location.LocationListener FusionLocationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            current = location;
            prefs.saveLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            requestAddress(context);
        }
    };
    public static String address;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static boolean disconnectRequest;
    private static Context context;
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

    public MCLocation(Context context) {
        MCLocation.context = context;
        prefs = new MCPreferences(context);
        disconnectRequest = false;
        mLocationRequest = getProvider(LocationRequest.PRIORITY_HIGH_ACCURACY);
        current = getBestFusionLocation(context);
    }

    private static LocationRequest getProvider(int accuracy){
        int interval, bestInterval, displacement;
        switch (accuracy){
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

    public static Location getBestFusionLocation(Context context) {
        Location last = null;
        if (mGoogleApiClient != null) {
            last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }else
        {
            last = new Location(LocationManager.NETWORK_PROVIDER);
            /*
             * Это нужно для получения хотя бы какой-то точки, пока LocationListener не прочухается
             * Цепляем либо последнюю определенную точку, либо координаты центра Москвы.
            */
            if(prefs == null){
                prefs = new MCPreferences(context);
            }
            LatLng latlng = prefs.getSavedLatLng();
            last.setLatitude(latlng.latitude);
            last.setLongitude(latlng.longitude);
            last.setAccuracy(1000);
        }
        return last;
    }

    private static void runLocationService(int accuracy){
        mLocationRequest = getProvider(accuracy);
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallback).addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }
        if(mGoogleApiClient.isConnected()) {
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

    public static void wakeup(Context context) {
        runLocationService(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void updateStatusBar() {
        String name = MCAccidents.auth.getName();
        if (name.length() > 0) {
            name += ": ";
        }
        Text.set(context, R.id.statusBarText, name + address);
        Startup.map.placeUser(context);
    }

    private static JsonRequest getAddressRequest(Location location) {
        Map<String, String> post = new HashMap<>();
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        return new JsonRequest("mcaccidents", "geocode", post, "", true);
    }

    private static void requestAddress(Context context) {
        if (Startup.isOnline()) {
            Location location = getBestFusionLocation(context);
            if (current == location) {
                return;
            }
            JsonRequest request = getAddressRequest(location);
            if (request != null) {
                (new GeoCodeRequest(context)).execute(request);
            }
        } else {
            Toast.makeText(context, Startup.context.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
    }
}

