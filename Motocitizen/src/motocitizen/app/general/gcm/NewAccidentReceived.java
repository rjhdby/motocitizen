package motocitizen.app.general.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.MyLocationManager;
import motocitizen.main.R;
import motocitizen.startup.MyPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.MyUtils;


public class NewAccidentReceived extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NewAccidentReceived() {
        super("Intent");
        Log.d("GCM RECEIVED", "2");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
/*
    }
    public NewAccidentReceived(Context context, Intent intent) {
    */
        Log.d("GCM RECEIVED", "3");
        MyPreferences prefs = new MyPreferences(this);
        if(prefs.getDoNotDisturb()){
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        Log.d("GCM RECEIVED", "4");
        Bundle extras = intent.getExtras();
        try {
            AccidentsGeneral.points.addPoint(new Accident(extras, this));
        } catch (Accident.MCPointException e) {
            e.printStackTrace();
        }
        String type = extras.getString("type");
        String message = extras.getString("message");
        String title = extras.getString("title");
        String idString = extras.getString("id");
        String latString = extras.getString("lat");
        String lngString = extras.getString("lon");
        extras.putInt("toDetails", Integer.parseInt(idString));
        int id;
        double lat, lng;
        if (MyUtils.isInteger(idString)) {
            id = Integer.valueOf(idString);
            lat = Double.parseDouble(latString);
            lng = Double.parseDouble(lngString);
        } else {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        double distance = MyLocationManager.getBestFusionLocation(this).distanceTo(MyUtils.LatLngToLocation(new LatLng(lat, lng))) / 1000;

        if (prefs.getAlarmDistance() < distance) {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        if (!prefs.toShowAccType(type)) {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        Intent notificationIntent = new Intent(this, Startup.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtras(extras);
        PendingIntent contentIntent = PendingIntent.getActivity(this, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Resources res = this.getResources();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.logo).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                .setTicker(title).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentText(message);

        if (prefs.getAlarmSoundTitle().equals("default system")) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        } else {
            builder.setSound(prefs.getAlarmSoundUri(), AudioManager.STREAM_NOTIFICATION);
            builder.setVibrate(new long[]{1000, 1000, 1000});
        }
        Notification notification = builder.getNotification();
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }
}
