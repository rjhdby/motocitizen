package motocitizen.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.LinkedList;
import java.util.Map;

import kotlin.Unit;
import motocitizen.content.AccidentsController;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.dictionary.Medicine;
import motocitizen.main.R;
import motocitizen.ui.activity.AccidentDetailsActivity;
import motocitizen.utils.Preferences;

public class NotificationListener extends FirebaseMessagingService {
    private static int                 ICON        = R.mipmap.ic_launcher;
    private static LinkedList<Integer> tray        = new LinkedList<>();
    private        Preferences         preferences = Preferences.INSTANCE;
    private NotificationManagerCompat notificationManager;
    private Accident                  accident;
    private int                       idHash;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map     data = remoteMessage.getData();
        Integer id   = Integer.parseInt(data.get("id").toString());
        Content.INSTANCE.requestSingleAccident(id, result -> {raiseNotification(id); return Unit.INSTANCE;});
    }

    private void raiseNotification(Integer id) {
        accident = Content.INSTANCE.accident(id);
        notificationManager = NotificationManagerCompat.from(this);
        if (doNotShow()) return;

        idHash = accident.location().hashCode();

        notificationManager.notify(idHash, makeNotification());
        manageTray();
    }

    private boolean doNotShow() {
        return accident == null || !accident.isVisible() || preferences.getDoNotDisturb();
    }

    private Intent makeIntent() {
        Intent intent = new Intent(this, AccidentDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident.getId());
        return intent;
    }

    private Notification makeNotification() {
        return new NotificationCompat.Builder(this)
                .setContentIntent(makePendingIntent())
                .setSmallIcon(ICON)
                .setLargeIcon(makeLargeIcon())
                .setTicker(accident.getAddress())
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(makeTitle())
                .setSound(makeSound())
                .setVibrate(makeVibration())
                .setContentText(accident.getAddress())
                .build();
    }

    private PendingIntent makePendingIntent() {
        return PendingIntent.getActivity(this, idHash, makeIntent(), PendingIntent.FLAG_ONE_SHOT);
    }

    private Uri makeSound() {
        return preferences.getSoundTitle().equals("default system")
               ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
               : preferences.getSound();
    }

    private long[] makeVibration() {
        return preferences.getVibration()
               ? new long[]{ 1000, 1000, 1000 }
               : new long[ 0 ];
    }

    private String makeTitle() {
        return String.format("%s%s(%s)", accident.getType().getText(), makeDamageString(), accident.distanceString());
    }

    private String makeDamageString() {
        return accident.getMedicine() == Medicine.UNKNOWN ? "" : ", " + accident.getMedicine().getText();
    }

    private void manageTray() {
        tray.push(idHash);
        while (tray.size() > preferences.getMaxNotifications()) {
            int remove = tray.pollLast();
            notificationManager.cancel(remove);
        }
    }

    private Bitmap makeLargeIcon() {
        return BitmapFactory.decodeResource(getResources(), ICON);
    }
}
