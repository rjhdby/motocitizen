package motocitizen.app.general.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.MyApp;
import motocitizen.app.general.MyLocationManager;
import motocitizen.main.R;
import motocitizen.startup.MyPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.MyUtils;

class Notification {
    public Notification(Context context, Intent intent) {
        MyPreferences prefs = ((MyApp) context.getApplicationContext()).getPreferences();
        if(prefs.getDoNotDisturb()){
            return;
        }
        Bundle extras = intent.getExtras();

        String type = extras.getString("type");
        String message = extras.getString("message");
        String title = extras.getString("title");
        String idString = extras.getString("id");
        String latString = extras.getString("lat");
        String lngString = extras.getString("lng");
        extras.putInt("toDetails", Integer.parseInt(idString));
        int id;
        double lat, lng;
        if (MyUtils.isInteger(idString)) {
            id = Integer.valueOf(idString);
            lat = Double.parseDouble(latString);
            lng = Double.parseDouble(lngString);
        } else {
            return;
        }
        double distance = MyLocationManager.getBestFusionLocation(context).distanceTo(MyUtils.LatLngToLocation(new LatLng(lat, lng))) / 1000;

        if (prefs.getAlarmDistance() < distance) {
            return;
        }
        if (!prefs.toShowAccType(type)) {
            return;
        }
        Intent notificationIntent = new Intent(context, Startup.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtras(extras);
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Resources res = context.getResources();
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.logo).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                .setTicker(title).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentText(message);

        if (prefs.getAlarmSoundTitle().equals("default system")) {
            builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        } else {
            builder.setSound(prefs.getAlarmSoundUri(), AudioManager.STREAM_NOTIFICATION);
            builder.setVibrate(new long[]{1000, 1000, 1000});
        }
        android.app.Notification notification = builder.getNotification();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}