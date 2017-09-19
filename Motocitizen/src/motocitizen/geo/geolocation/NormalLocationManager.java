package motocitizen.geo.geolocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import kotlin.Unit;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.datasources.network.requests.InPlaceRequest;
import motocitizen.datasources.network.requests.LeaveRequest;
import motocitizen.datasources.preferences.Preferences;
import motocitizen.geo.MyGoogleApiClient;
import motocitizen.geo.geocoder.AddressResolver;
import motocitizen.ui.activity.MainScreenActivity;
import motocitizen.user.User;
import motocitizen.utils.LocationUtils;

public class NormalLocationManager implements SecuredLocationManagerInterface {
    /* constants */
    private static final int ARRIVED_MAX_ACCURACY = 200;
    /* end constants */

    private Location current;

    NormalLocationManager() {
        current = getLocation();
    }

    private Unit locationListener(Location location) {
        current = location;
        Preferences.INSTANCE.setSavedLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        requestAddress();
        checkInPlace(location);
        return Unit.INSTANCE;
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

    @SuppressWarnings({ "MissingPermission" })
    public Location getLocation() {
        Location result = MyGoogleApiClient.INSTANCE.getLastLocation();
        if (result != null) current = result;
        return current;
    }

    @Override
    public String getAddress(LatLng location) {
        return AddressResolver.INSTANCE.getAddress(location);
    }

    public String getCurrentAddress() {
        return getAddress(LocationUtils.toLatLng(current));
    }

    private void runLocationService(int accuracy) {
        MyGoogleApiClient.INSTANCE.runLocationService(getProvider(accuracy), this::locationListener);
    }

    public void sleep(Context context) {
        runLocationService(LocationRequest.PRIORITY_LOW_POWER);
    }

    public void wakeup(Context context) {
        runLocationService(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //todo exterminatus
    private void requestAddress() {
        Location location = getLocation();
        if (current == location) return;
        MainScreenActivity.updateStatusBar(getCurrentAddress());
    }

    private void checkInPlace(Location location) {
        String login = User.INSTANCE.getName();
        if (login.equals("")) return;
        int currentInPlace = Content.INSTANCE.getInPlace();
        if (currentInPlace != 0) {
            if (isInPlace(location, currentInPlace)) return;
//            Content.INSTANCE.setLeave(currentInPlace); //todo
            new LeaveRequest(currentInPlace, (result) -> Unit.INSTANCE);
        }
        //todo refactor
        for (int id : Content.INSTANCE.getIds()) {
            if (id == currentInPlace) continue;
            if (isArrived(location, id)) {
                Content.INSTANCE.setInPlace(id);
                new InPlaceRequest(id, (result) -> Unit.INSTANCE);
            }
        }
    }

    private boolean isArrived(Location location, int accId) {
        Accident acc = Content.INSTANCE.accident(accId);
        return LocationUtils.distanceTo(acc.getCoordinates(), location) < Math.max(ARRIVED_MAX_ACCURACY, location.getAccuracy());
    }

    private boolean isInPlace(Location location, int accId) {
        Accident acc = Content.INSTANCE.accident(accId);
        return location != null && (LocationUtils.distanceTo(acc.getCoordinates(), location) - location.getAccuracy() < 100);
    }
}
