package motocitizen.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import motocitizen.MyApp;
import motocitizen.content.Content;
import motocitizen.network.requests.InplaceRequest;
import motocitizen.network.requests.LeaveRequest;
import motocitizen.startup.Preferences;
import motocitizen.startup.Startup;

public class MyLocationManager {
    public static  String          address;
    private static Location        current;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static Context         context;
    private static final com.google.android.gms.location.LocationListener FusionLocationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            current = location;
            Preferences.saveLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            requestAddress(context);
            checkInPlace(context, location);
        }
    };
    private static final GoogleApiClient.ConnectionCallbacks              connectionCallback     = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle connectionHint) {
//TODO Это пиздец
            while (!mGoogleApiClient.isConnected()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusionLocationListener);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, FusionLocationListener);
            current = getLocation();
        }

        @Override
        public void onConnectionSuspended(int arg0) {

        }
    };

    public MyLocationManager(Context context) {
        MyLocationManager.context = context;
        mLocationRequest = getProvider(LocationRequest.PRIORITY_HIGH_ACCURACY);
        current = getLocation();
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

    public static Location getLocation() {
        Location last = null;
        if (mGoogleApiClient != null) {
            last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (last == null) {
            last = new Location(LocationManager.NETWORK_PROVIDER);
            LatLng latlng = Preferences.getSavedLatLng();
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
        Location location = getLocation();
        if (current == location) return;
        address = ((MyApp) context.getApplicationContext()).getAddress(location);
        Startup.updateStatusBar(MyLocationManager.address);
    }

    private static void checkInPlace(Context context, Location location) {
        String login = Preferences.getLogin();
        if (login.equals("")) return;
        int currentInplace = Content.getInplaceID();
        if (currentInplace != 0) {
            if (isInPlace(location, currentInplace)) return;
            Content.setLeave(currentInplace);
            new LeaveRequest(context, currentInplace);
        }
        for (int accId : Content.getPoints().keySet()) {
            if (accId == currentInplace) continue;
            if (isArrived(location, accId)) {
                Content.setInPlace(accId);
                new InplaceRequest(context, accId);
            }
        }
    }

    private static boolean isArrived(Location location, int accId) {
        double meters = Content.getPoint(accId).getLocation().distanceTo(location);
        double limit = Math.max(300, location.getAccuracy());
        return meters < limit;
    }

    private static void message(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private static boolean isInPlace(Location location, int accId) {
        motocitizen.accident.Accident acc = Content.getPoint(accId);
        if (acc == null) {
            message("Invalid accident");
            return false;
        }
        if (location == null) {
            message("Invalid location");
            return false;
        }
        if (acc.getLocation() == null) {
            message("Invalid accident location");
            return false;
        }

        double meters = Content.getPoint(accId).getLocation().distanceTo(location);
        double limit = location.getAccuracy() * 2 + 1000;
        return meters < limit;
    }
}
