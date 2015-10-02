package motocitizen.geolocation;

import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import motocitizen.MyApp;
import motocitizen.content.Content;
import motocitizen.network.requests.InplaceRequest;
import motocitizen.network.requests.LeaveRequest;
import motocitizen.utils.Preferences;
import motocitizen.Activity.MainScreenActivity;

public class MyLocationManager {
    /* constants */
    private static final int LOW_INTERVAL     = 60000;
    private static final int LOW_BEST         = 30000;
    private static final int LOW_DISPLACEMENT = 200;

    private static final int HIGH_INTERVAL     = 5000;
    private static final int HIGH_BEST         = 1000;
    private static final int HIGH_DISPLACEMENT = 10;

    private static final int DEFAULT_ACCURACY     = 1000;
    private static final int ARRIVED_MAX_ACCURACY = 300;
    /* end constants */

    public static        String                              address;
    private static       Location                            current;
    private static       GoogleApiClient                     googleApiClient;
    private static       LocationRequest                     locationRequest;
    private static final LocationListener                    locationListener;
    private static final GoogleApiClient.ConnectionCallbacks connectionCallback;

    static {
        connectionCallback = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
//TODO Это пиздец
                while (!googleApiClient.isConnected()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
                current = getLocation();
            }

            @Override
            public void onConnectionSuspended(int arg0) { }
        };
        locationListener = new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                current = location;
                Preferences.saveLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                requestAddress();
                checkInPlace(location);
            }
        };
    }

    public MyLocationManager() {
        locationRequest = getProvider(LocationRequest.PRIORITY_HIGH_ACCURACY);
        current = getLocation();
    }

    private static LocationRequest getProvider(int accuracy) {
        int interval, bestInterval, displacement;
        switch (accuracy) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                interval = HIGH_INTERVAL;
                bestInterval = HIGH_BEST;
                displacement = HIGH_DISPLACEMENT;
                break;
            case LocationRequest.PRIORITY_LOW_POWER:
            default:
                interval = LOW_INTERVAL;
                bestInterval = LOW_BEST;
                displacement = LOW_DISPLACEMENT;
        }
        LocationRequest lr = new LocationRequest();
        lr.setInterval(interval);
        lr.setFastestInterval(bestInterval);
        lr.setSmallestDisplacement(displacement);
        lr.setPriority(accuracy);
        return lr;
    }

    public static Location getDirtyLocation(){
        if (current != null && current.getTime() - (new Date()).getTime() < 30000) return current;
        return getLocation();
    }

    public static Location getLocation() {
        if (googleApiClient != null) {
            current = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        if (current == null) {
            current = new Location(LocationManager.NETWORK_PROVIDER);
            LatLng latlng = Preferences.getSavedLatLng();
            current.setLatitude(latlng.latitude);
            current.setLongitude(latlng.longitude);
            current.setAccuracy(DEFAULT_ACCURACY);
        }
        return current;
    }

    private static void runLocationService(int accuracy) {
        locationRequest = getProvider(accuracy);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MyApp.getAppContext()).addConnectionCallbacks(connectionCallback).addApi(LocationServices.API).build();
            googleApiClient.connect();
        }
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        } else {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    public static void sleep() {
        runLocationService(LocationRequest.PRIORITY_LOW_POWER);
    }

    public static void wakeup() {
        runLocationService(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private static void requestAddress() {
        Location location = getLocation();
        if (current == location) return;
        address = getAddress(location);
        MainScreenActivity.updateStatusBar(MyLocationManager.address);
    }

    private static void checkInPlace(Location location) {
        String login = Preferences.getLogin();
        if (login.equals("")) return;
        int currentInplace = Content.getInplaceID();
        if (currentInplace != 0) {
            if (isInPlace(location, currentInplace)) return;
            Content.setLeave(currentInplace);
            new LeaveRequest(currentInplace);
        }
        for (int accId : Content.getPoints().keySet()) {
            if (accId == currentInplace) continue;
            if (isArrived(location, accId)) {
                Content.setInPlace(accId);
                new InplaceRequest(accId);
            }
        }
    }

    private static boolean isArrived(Location location, int accId) {
        double meters = Content.getPoint(accId).getLocation().distanceTo(location);
        double limit  = Math.max(ARRIVED_MAX_ACCURACY, location.getAccuracy());
        return meters < limit;
    }

    private static void message(String text) {
        Toast.makeText(MyApp.getCurrentActivity(), text, Toast.LENGTH_LONG).show();
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
        double limit  = location.getAccuracy() * 2 + 1000;
        return meters < limit;
    }
    public static String getAddress(Location location) {
        StringBuilder res = new StringBuilder();
        try {
            List<Address> list;
            list = MyApp.geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (list == null || list.size() == 0)
                return location.getLatitude() + " " + location.getLongitude();
            Address address = list.get(0);
            String locality = address.getLocality();
            if (locality == null) locality = address.getAdminArea();
            if (locality == null && address.getMaxAddressLineIndex() > 0)
                locality = address.getAddressLine(0);

            String thoroughfare = address.getThoroughfare();
            if (thoroughfare == null) thoroughfare = address.getSubAdminArea();

            String featureName = address.getFeatureName();

            if (locality != null) res.append(locality);
            if (thoroughfare != null) {
                if (res.length() > 0) res.append(" ");
                res.append(thoroughfare);
            }
            if (featureName != null) if (res.length() > 0) res.append(" ");
            res.append(featureName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
