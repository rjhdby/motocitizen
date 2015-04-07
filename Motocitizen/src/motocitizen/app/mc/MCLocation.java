package motocitizen.app.mc;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.GeoCodeRequest;
import motocitizen.network.JsonRequest;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

public class MCLocation {
    private static final String TAG = "LOCATION";
    public static Location current;
    private static final com.google.android.gms.location.LocationListener FusionLocationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            current = location;
            requestAddress(Startup.context);
        }
    };
    public static String address;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static boolean disconnectRequest;
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
            if (disconnectRequest) {
                mGoogleApiClient.disconnect();
                return;
            }
            Log.d(TAG, "Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, FusionLocationListener);
            current = getBestFusionLocation(Startup.context);
        }

        @Override
        public void onConnectionSuspended(int arg0) {

        }
    };

    public MCLocation(Context context) {
        disconnectRequest = false;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        current = getBestFusionLocation(context);
        // zz
        // requestAddress(context);
        //zz
        //Startup.map.jumpToPoint(current);
    }

    public static Location getBestFusionLocation(Context context) {
        Location last = null;
        double lastLon, lastLat;
        if (mGoogleApiClient != null) {
            last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (last == null) {
            //TODO Грязный хак, нужно придумать как работать без имени файла
            SharedPreferences prefs = context.getSharedPreferences("motocitizen.main_preferences", Context.MODE_PRIVATE);
            last = new Location(LocationManager.NETWORK_PROVIDER);
/*            if (prefs == null) {
                lastLon = 37.622735;
                lastLat = 55.752295;
                Log.d(TAG, "FAKE");
            } else {*/

            //TODO Понять для чего это нужно, т.к. больше ни где не используется.
            lastLon = (double) prefs.getFloat("lastLon", 37.622735f);
            lastLat = (double) prefs.getFloat("lastLat", 55.752295f);
            if (lastLon == 37.622735f) {
                Log.d(TAG, "FAKE");
//                }
            }
            last.setLatitude(lastLat);
            last.setLongitude(lastLon);
            last.setAccuracy(10000);
        }
        return last;
    }

    public static void sleep() {
        if (mGoogleApiClient == null) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusionLocationListener);
            mGoogleApiClient.disconnect();
        } else if (mGoogleApiClient.isConnecting()) {
            disconnectRequest = true;
        }
    }

    public static void wakeup(Context context) {
        disconnectRequest = false;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallback).addApi(LocationServices.API).build();
        }
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    public static void updateStatusBar() {
        String name = MCAccidents.auth.name;
        if (name.length() > 0) {
            name += ": ";
        }
        Text.set(R.id.statusBarText, name + address);
        Startup.map.placeUser(Startup.context);
    }

    private static JsonRequest getAddressRequest(Location location) {
        Map<String, String> post = new HashMap<>();
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        JsonRequest res = new JsonRequest("mcaccidents", "geocode", post, "", true);
        return res;
    }

    private static void requestAddress(Context context) {
        if (Startup.isOnline()) {
            Location location = getBestFusionLocation(context);
            if (current == location) {
                return;
            }
            JsonRequest request = getAddressRequest(location);
            if (request != null) {
                (new GeoCodeRequest(Startup.context)).execute(request);
            }
        } else {
            Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
    }
}

