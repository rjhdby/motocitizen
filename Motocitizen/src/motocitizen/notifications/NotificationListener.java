package motocitizen.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.LinkedList;
import java.util.Map;

import motocitizen.accident.Accident;
import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.main.R;
import motocitizen.utils.Preferences;

public class NotificationListener extends FirebaseMessagingService {
    private static LinkedList<Integer> tray = new LinkedList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data = remoteMessage.getData();
        Content.getInstance()
               .requestUpdate(result -> {
                   if (result.has("error")) return;
                   Content.getInstance().parseJSON(result);
                   raiseNotification(data);
               });
    }

    private void raiseNotification(Map data) {
        try {
            int      id       = Integer.parseInt(data.get("id").toString());
            Accident accident = Content.getInstance().get(id);
            if (accident == null || accident.isInvisible() || Preferences.getInstance().getDoNotDisturb()) return;
            Intent notificationIntent = new Intent(this, AccidentDetailsActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra("accidentID", id);

            int           idHash        = accident.getLocation().hashCode();
            PendingIntent contentIntent = PendingIntent.getActivity(this, idHash, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            Resources     res           = this.getResources();

            String damage = accident.getMedicine() == Medicine.UNKNOWN ? "" : ", " + accident.getMedicine().string();
            String title  = String.format("%s%s(%s)", accident.getType().string(), damage, accident.getDistanceString());
            Uri sound = Preferences.getInstance().getAlarmSoundTitle().equals("default system")
                        ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        : Preferences.getInstance().getAlarmSoundUri();
            long[] vibrate = Preferences.getInstance().getVibration()
                             ? new long[]{ 1000, 1000, 1000 }
                             : new long[ 0 ];

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setTicker(accident.getAddress())
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setSound(sound)
                    .setVibrate(vibrate)
                    .setContentText(accident.getAddress())
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(idHash, notification);
            tray.push(idHash);
            while (tray.size() > Preferences.getInstance().getMaxNotifications()) {
                int remove = tray.pollLast();
                notificationManager.cancel(remove);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
