package motocitizen.app.mc.gcm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.notification.MCNotification;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Text;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MCGCMRegistration {
	private static final String TAG = "GCM";
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "mc.gcm.id";
	private static final String PROPERTY_APP_VERSION = "mc.app.version";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "258135342835";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	String regid;

	public MCGCMRegistration() {

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(Startup.context);
			regid = getRegistrationId();
			Log.d(TAG, regid);
			if (regid.isEmpty()) {
				registerInBackground();
			} else {
				storeRegistrationId(regid);
			}
		} else {
			Log.d(TAG, "No valid Google Play Services APK found.");
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Startup.context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) Startup.context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.d(TAG, "This device is not supported.");
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId() {
		// final SharedPreferences prefs = getGCMPreferences(Startup.context);
		String registrationId = Startup.prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.d(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing registration ID is not guaranteed to work with
		// the new app version.
		int registeredVersion = Startup.prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.d(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion() {
		try {
			PackageInfo packageInfo = Startup.context.getPackageManager().getPackageInfo(Startup.context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(Startup.context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					sendRegistrationIdToBackend();
					storeRegistrationId(regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// new MCNotification(msg);
				Log.d(TAG, msg);
			}

		}.execute(null, null, null);
	}

	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	private void storeRegistrationId(String regId) {
		int appVersion = getAppVersion();
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = Startup.prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
		JSONObject json = new JSONCall("mcaccidents", "registerGCM").request(createPOST(regId));
	}
	private static Map<String, String> createPOST(String regId) {
		Map<String, String> POST = new HashMap<String, String>();
		POST.put("owner_id", String.valueOf(MCAccidents.auth.id));
		POST.put("gcm_key", regId);
		POST.put("login", MCAccidents.auth.getLogin());
		POST.put("passhash", MCAccidents.auth.makePassHash());
		POST.put("calledMethod", "registerGCM");
		return POST;
	}
}