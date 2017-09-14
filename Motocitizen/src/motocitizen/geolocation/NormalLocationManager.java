package motocitizen.geolocation;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import kotlin.Unit;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.datasources.network.requests.InPlaceRequest;
import motocitizen.datasources.network.requests.LeaveRequest;
import motocitizen.geocoder.MyGeoCoder;
import motocitizen.ui.activity.MainScreenActivity;
import motocitizen.user.User;
import motocitizen.utils.LocationUtils;
import motocitizen.utils.Preferences;

public class NormalLocationManager implements SecuredLocationManagerInterface {
    /* constants */
    private static final int ARRIVED_MAX_ACCURACY = 300;
    /* end constants */

    private Location                            current;
    private GoogleApiClient                     googleApiClient;
    private LocationRequest                     locationRequest;
    private LocationListener                    locationListener;
    private GoogleApiClient.ConnectionCallbacks connectionCallback;

    public static boolean showDialogExact = false;

    NormalLocationManager() {
        current = getLocation();
    }

    private void setup() {
        if (connectionCallback == null) connectionCallback = new MyConnectionCallback();
        if (locationListener == null) locationListener = location -> {
            current = location;
            Preferences.INSTANCE.setSavedLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            requestAddress();
            checkInPlace(location);
        };
        if (locationRequest == null)
            locationRequest = getProvider(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private LocationRequest getProvider(int accuracy) {
        switch (accuracy) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                return LocationRequestFactory.INSTANCE.accurate();
            case LocationRequest.PRIORITY_LOW_POWER:
            default:
                return LocationRequestFactory.INSTANCE.coarse();
        }
    }


    @SuppressWarnings({"MissingPermission"})
    public Location getLocation() {
        if (googleApiClient != null) {
            current = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        return current;
    }

    @SuppressWarnings({"MissingPermission"})
    private void runLocationService(Context context, int accuracy) {
        setup();
        locationRequest = getProvider(accuracy);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallback).addApi(LocationServices.API).build();
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

    public void sleep(Context context) {
        runLocationService(context, LocationRequest.PRIORITY_LOW_POWER);
    }

    public void wakeup(Context context) {
        runLocationService(context, LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void requestAddress() {
        Location location = getLocation();
        if (current == location) return;
        MainScreenActivity.updateStatusBar(getAddress(LocationUtils.Location2LatLng(location)));
    }

    private void checkInPlace(Location location) {
        String login = User.INSTANCE.getName();
        if (login.equals("")) return;
        int currentInplace = Content.INSTANCE.getInPlace();
        if (currentInplace != 0) {
            if (isInPlace(location, currentInplace)) return;
//            Content.INSTANCE.setLeave(currentInplace); //todo
            new LeaveRequest(currentInplace, (result) -> Unit.INSTANCE);
        }
        for (int accId : Content.INSTANCE.getAccidents().keySet()) {
            if (accId == currentInplace) continue;
            if (isArrived(location, accId)) {
                Content.INSTANCE.setInPlace(accId);
                new InPlaceRequest(accId, (result) -> Unit.INSTANCE);
            }
        }
    }

    private boolean isArrived(Location location, int accId) {
        return Content.INSTANCE.getAccidents().get(accId).location().distanceTo(location) < Math.max(ARRIVED_MAX_ACCURACY, location.getAccuracy());
    }

    private boolean isInPlace(Location location, int accId) {
        Accident acc = Content.INSTANCE.getAccidents().get(accId);
        return acc != null && location != null && (acc.location().distanceTo(location) - location.getAccuracy() < 100);
    }

    public String getAddress(LatLng location) {
        try {
            List<Address> list = findAddressByLocation(location);
            if (list == null || list.size() == 0) {
                showDialogExact = true;
                return location.longitude + " " + location.longitude;
            }

            return buildAddressString(list.get(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String buildAddressString(Address address) {
        return (new StringBuilder())
                .append(extractLocality(address))
                .append(" ")
                .append(extractThoroughfare(address))
                .append(" ")
                .append(address.getFeatureName() != null ? address.getFeatureName() : "")
                .toString()
                .trim();
    }

    private List<Address> findAddressByLocation(LatLng location) throws IOException {
        return MyGeoCoder.getInstance().getFromLocation(location.latitude, location.longitude, 1);
    }

    private String extractLocality(Address address) {
        if (address.getLocality() != null) return address.getLocality();
        if (address.getAdminArea() != null) return address.getAdminArea();
        if (address.getMaxAddressLineIndex() > 0) return address.getAddressLine(0);
        return "";
    }

    private String extractThoroughfare(Address address) {
        if (address.getThoroughfare() != null) return address.getThoroughfare();
        if (address.getSubAdminArea() != null) return address.getSubAdminArea();
        return "";
    }

    @SuppressWarnings({"MissingPermission"})
    private class MyConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
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
        public void onConnectionSuspended(int arg0) {
        }
    }
}
