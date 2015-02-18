package motocitizen.app.mc;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import motocitizen.startup.Startup;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class MCPush extends BroadcastReceiver{
	private final static String APIKEY = "AIzaSyDs6P7xDgBI9VNVkt9bvz_YNA-XEBmRNQc";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String SENDER_ID = "258135342835";
    static final String TAG = "GCMDemo";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    
    
    
    private final static Context context = Startup.context;
    
    public MCPush(){
    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");

    	registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));

    	registrationIntent.putExtra("sender", SENDER_ID);

    	Startup.context.startService(registrationIntent);
    	/*
    	 if (checkPlayServices()) {
             gcm = GoogleCloudMessaging.getInstance(Startup.context);
             regid = getRegistrationId();

             if (regid.isEmpty()) {
                 registerInBackground();
             }
         } else {
             Log.i(TAG, "No valid Google Play Services APK found.");
         }
         */
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}
}
