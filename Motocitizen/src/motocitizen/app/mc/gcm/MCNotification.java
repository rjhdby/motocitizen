package motocitizen.app.mc.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import motocitizen.app.mc.MCAccTypes;
import motocitizen.app.mc.MCLocation;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.MCUtils;

class MCNotification {
    @SuppressWarnings("deprecation")
    public MCNotification(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        String type = extras.getString("type");
        String message = extras.getString("message");
        String title = extras.getString("title");
        String idString = extras.getString("id");
        String latString = extras.getString("lat");
        String lngString = extras.getString("lng");
        int id;
        double lat, lng;
        if (MCUtils.isInteger(idString)) {
            id = Integer.valueOf(idString);
            lat = Double.parseDouble(latString);
            lng = Double.parseDouble(lngString);
        } else {
            return;
        }
        double distance = MCLocation.getBestFusionLocation(context).distanceTo(MCUtils.LatLngToLocation(new LatLng(lat, lng))) / 1000;
        SharedPreferences prefs = context.getSharedPreferences("motocitizen.startup", Context.MODE_PRIVATE);
        if (prefs.getInt("mc.distance.alarm", 0) < distance) {
            return;
        }
        if (!MCAccTypes.get(type).enabled) {
            return;
        }
        Intent notificationIntent = new Intent(context, Startup.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtras(extras);
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.logo).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                .setTicker(title).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentText(message);

        String sound = prefs.getString("mc.notification.sound", "default system");
        if (sound.equals("default system")) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        } else {
            builder.setSound(Uri.parse(sound), AudioManager.STREAM_NOTIFICATION);
            builder.setVibrate(new long[]{1000, 1000, 1000});
        }
        Notification notification = builder.getNotification();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}