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

import java.util.LinkedList;

import motocitizen.activity.MainScreenActivity;
import motocitizen.accident.Accident;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.main.R;
import motocitizen.utils.Preferences;


public class NewAccidentReceived extends IntentService {
    private static NotificationManager notificationManager;
    private static LinkedList<Integer> tray = new LinkedList<>();

    public NewAccidentReceived() {
        super("Intent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Content.getInstance();
        parsePush(intent.getExtras());
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void parsePush(Bundle extras) {
        Accident accident = new Accident(extras);

        if (!accident.isNoError()) return;
        Content.getInstance().put(accident.getId(), accident);

        if (accident.isInvisible() || Preferences.getInstance().getDoNotDisturb()) return;

        Intent notificationIntent = new Intent(this, MainScreenActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtras(extras);

        int           idHash        = accident.getLocation().hashCode();
        PendingIntent contentIntent = PendingIntent.getActivity(this, idHash, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Resources     res           = this.getResources();


        String title;
        if (accident.getMedicine() == Medicine.UNKNOWN)
            title = String.format("%s(%s)", accident.getType().string(), accident.getDistanceString());
        else
            title = String.format("%s, %s(%s)", accident.getType().string(), accident.getMedicine().string(), accident.getDistanceString());

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
               .setSmallIcon(R.mipmap.ic_launcher)
               .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
               .setTicker(accident.getAddress())
               .setWhen(System.currentTimeMillis())
               .setAutoCancel(true)
               .setContentTitle(title)
               .setContentText(accident.getAddress());

        if (Preferences.getInstance().getAlarmSoundTitle().equals("default system")) {
            builder.setDefaults(Preferences.getInstance().getVibration() ? Notification.DEFAULT_ALL : Notification.DEFAULT_SOUND);
        } else {
            setSound(builder);
            if (Preferences.getInstance().getVibration()) builder.setVibrate(new long[]{ 1000, 1000, 1000 });
        }
        Notification notification = getNotification(builder);

        if (notificationManager == null)
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(idHash, notification);
        tray.push(idHash);
        while (tray.size() > Preferences.getInstance().getMaxNotifications()) {
            int remove = tray.pollLast();
            notificationManager.cancel(remove);
        }
    }

    @SuppressWarnings("deprecation")
    private void setSound(Notification.Builder builder) {
        if (Build.VERSION.SDK_INT < 21) {
            builder.setSound(Preferences.getInstance().getAlarmSoundUri(), AudioManager.STREAM_NOTIFICATION);
        } else {
            builder.setSound(Preferences.getInstance().getAlarmSoundUri(), (new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)).build());
        }
    }

    @SuppressWarnings("deprecation")
    private Notification getNotification(Notification.Builder builder) {
        if (Build.VERSION.SDK_INT < 16) {
            return builder.getNotification();
        } else {
            return builder.build();
        }
    }

    public static void removeFromTray(int idHash) {
        tray.remove(idHash);
    }
}
