package motocitizen.tasks;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationService extends TimerTask {

	private Activity act;
	public double lat, lng;
	public float acc;
	private static LocationManager lm;
	public Criteria criteria;
	public String currentProvider;
	public Location location;
	private int refresh;
	private int distance;
	private List<String> actions;
	private static LocationListener nl, gl;
	private static Location lastKnownLocation;

	public LocationService(String mode) {
		super();
		makeActionsList();
		act = (Activity) Startup.context;
		lm = (LocationManager) Startup.context.getSystemService(Context.LOCATION_SERVICE);
		criteria = makeCriteria(mode);
		currentProvider = checkProvider();
		lastKnownLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		nl = networkListener();
		gl = gpsListener();
		runListener();
	}

	private void makeActionsList() {
		actions = new ArrayList<String>();
		for (String prop : Startup.props.keys("task.locationservice.")) {
			String[] parts = prop.split("[.]");
			if (parts[2].equals("onlocationchange")) {
				String app = parts[3];
				// Log.d("LOCATION SERVICE", prop + ": " +
				if (Startup.props.get("app." + app + ".enabled").equals("true")) {
					actions.add(Startup.props.get(prop));
				}
			}
		}
	}

	@Override
	public void run() {
		if (!currentProvider.equals(checkProvider())) {
			runListener();
		}
	}

	public void runListener(final String provider) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lm.requestLocationUpdates(currentProvider, refresh, distance, getListener(provider));
			}
		});
	}

	public void stop() {
		lm.removeUpdates(nl);
		lm.removeUpdates(gl);
	}

	public void wakeUp() {
		changeMode("high");
	}

	public void sleep() {
		lm.removeUpdates(gl);
		changeMode("low");
	}

	public void changeMode(String mode) {
		criteria = makeCriteria(mode);
		runListener();
	}

	private void runListener() {
		currentProvider = checkProvider();
		runListener(currentProvider);
	}

	public static Location getLocation() {
		return lastKnownLocation;
	}

	private String checkProvider() {
		String provider = lm.getBestProvider(criteria, true);
		return provider;
	}

	private LocationListener getListener(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			return gl;
		} else {
			return nl;
		}
	}

	private Criteria makeCriteria(String mode) {
		Criteria criteria = new Criteria();
		if (mode.equals("low")) {
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			this.refresh = 20000;
			this.distance = 100;
		} else {
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			this.refresh = 1000;
			this.distance = 5;
		}
		return criteria;
	}

	private LocationListener gpsListener() {
		return new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				runOnLocationChangeActions(getBestLocation(location));
				lm.removeUpdates(nl);
				Log.d("LISTENER", currentProvider);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		};
	}

	private LocationListener networkListener() {
		return new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				runOnLocationChangeActions(getBestLocation(location));
				if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					lm.removeUpdates(gl);
				}
				Log.d("LISTENER", currentProvider);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		};
	}

	public Location getBestLocation(Location location) {
		Location last;
		int d;
		last = lastKnownLocation;
		// getBestLastLocation();
		if (last.getAccuracy() >= location.getAccuracy()) {
			lastKnownLocation = location;
		} else {
			d = (int) last.distanceTo(location);
			if (d > (location.getAccuracy())) {
				lastKnownLocation = location;
			}
		}
		return lastKnownLocation;
	}

	private void runOnLocationChangeActions(Location location) {
		for (String action : actions) {
			Log.d("ACTION", "LOCATION CHANGE" + action);
			try {
				Class<?> actionObj;
				actionObj = Class.forName(action);
				actionObj.getMethod("action", Location.class).invoke(null, lastKnownLocation);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
