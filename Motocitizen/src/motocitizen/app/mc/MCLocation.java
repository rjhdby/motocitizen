package motocitizen.app.mc;

import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.maps.general.MCMap;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Text;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MCLocation {
	public static Location current;
	public static String address;

	private static GoogleApiClient mGoogleApiClient;
	public static final String TAG = "LOCATION";
	private static LocationRequest mLocationRequest;
	private static boolean disconnectRequest;

	public MCLocation(Context context) {
		disconnectRequest = false;
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(5000);
		mLocationRequest.setFastestInterval(1000);
		mLocationRequest.setSmallestDisplacement(10);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		current = getBestFusionLocation(context);
		setAddress(context);
		MCMap.jumpToPoint(current);
	}

	private static GoogleApiClient.ConnectionCallbacks connectionCallback = new GoogleApiClient.ConnectionCallbacks() {

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

	public static Location getBestFusionLocation(Context context) {
		Location last = null;
		double lastLon, lastLat;
		if (mGoogleApiClient != null) {
			last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		}
		if (last == null) {
			SharedPreferences prefs = context.getSharedPreferences("motocitizen.startup", Context.MODE_PRIVATE);
			last = new Location(LocationManager.NETWORK_PROVIDER);
			if (prefs == null) {
				lastLon = 37.622735;
				lastLat = 55.752295;
				Log.d(TAG, "FAKE");
			} else {
				lastLon = (double) prefs.getFloat("lastLon", 37.622735f);
				lastLat = (double) prefs.getFloat("lastLat", 55.752295f);
				if (lastLon == 37.622735f) {
					Log.d(TAG, "FAKE");
				}
			}
			last.setLatitude(lastLat);
			last.setLongitude(lastLon);
			last.setAccuracy(10000);
		}
		return last;
	}

	public static void sleep() {
		if (mGoogleApiClient == null) {
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

	private static com.google.android.gms.location.LocationListener FusionLocationListener = new com.google.android.gms.location.LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			current = location;
			perfomActions(Startup.context);
		}
	};

	private static void perfomActions(Context context) {
		String name;
		setAddress(context);
		name = MCAccidents.auth.name;
		if (!name.equals("")) {
			name += ": ";
		}
		Text.set(R.id.statusBarText, name + address);
		MCMap.placeUser(context);
	}

	public static String getAddress(Location location) {
		String address;
		Map<String, String> post = new HashMap<String, String>();
		post.put("lat", String.valueOf(location.getLatitude()));
		post.put("lon", String.valueOf(location.getLongitude()));
		JSONObject json = new JSONCall("mcaccidents", "geocode").request(post);
		try {
			address = json.getString("address");
		} catch (JSONException e) {
			address = "Ошибка геокодирования";
			e.printStackTrace();
		}
		return address;
	}

	private static void setAddress(Context context) {
		Location temp = getBestFusionLocation(context);
		if (current == temp) {
			return;
		}
		address = getAddress(current);
	}
}
