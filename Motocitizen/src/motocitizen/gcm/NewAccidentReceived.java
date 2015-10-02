package motocitizen.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;

import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.main.R;
import motocitizen.utils.Preferences;
import motocitizen.Activity.MainScreenActivity;


public class NewAccidentReceived extends IntentService {
    private static NotificationManager notificationManager;

    public NewAccidentReceived() {
        super("Intent");
    }

    public static void clearAll() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) MyApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Preferences.setNotificationList(new JSONArray());
        notificationManager.cancelAll();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Preferences.getDoNotDisturb()) {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        Bundle extras = intent.getExtras();
        try {
            if (!Content.isInitialized()) new Content();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Accident accident = new Accident(extras);
        if (!accident.isNoError()) {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        Content.getPoints().put(accident.getId(), accident);

        if (accident.isInvisible()) {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }

        Intent notificationIntent = new Intent(this, MainScreenActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtras(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(this, accident.getId(), notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Resources res = this.getResources();
        Notification.Builder builder = new Notification.Builder(this);

        String title = accident.getType().toString();
        if (accident.getMedicine() != Medicine.UNKNOWN)
            title += ", " + accident.getMedicine().toString();
        title += "(" + accident.getDistanceString() + ")";
        builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.logo).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo)).setTicker(accident.getAddress()).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentText(accident.getAddress());

        if (Preferences.getAlarmSoundTitle().equals("default system")) {
            builder.setDefaults(Preferences.getVibration() ? Notification.DEFAULT_ALL : Notification.DEFAULT_SOUND);
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                builder.setSound(Preferences.getAlarmSoundUri(), AudioManager.STREAM_NOTIFICATION);
            } else {
                builder.setSound(Preferences.getAlarmSoundUri(), (new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)).build());
            }
            if (Preferences.getVibration()) builder.setVibrate(new long[]{1000, 1000, 1000});
        }
        Notification notification;
        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }
        if (notificationManager == null)
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(accident.getId(), notification);
        manageTray(accident.getId());
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void manageTray(int id) {
        JSONArray tray = Preferences.getNotificationList();
        tray.put(String.valueOf(id));
        int max = Preferences.getMaxNotifications();
        if (max > tray.length()) {
            Preferences.setNotificationList(tray);
            return;
        }
        JSONArray out = new JSONArray();
        for (int i = 0; i < tray.length() - max; i++) {
            try {
                int idToCancel = Integer.parseInt(tray.getString(i));
                notificationManager.cancel(idToCancel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = tray.length() - max; i < tray.length(); i++) {
            try {
                out.put(tray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tray = out;
        Preferences.setNotificationList(tray);
    }
}
