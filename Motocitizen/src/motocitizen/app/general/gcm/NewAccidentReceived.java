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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.MyLocationManager;
import motocitizen.main.R;
import motocitizen.startup.MyPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.MyUtils;


public class NewAccidentReceived extends IntentService {
    //public static Queue<Integer> queue;
    private static NotificationManager notificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NewAccidentReceived() {
        super("Intent");
    }

    private MyPreferences prefs;

    @Override
    protected void onHandleIntent(Intent intent) {
/*
        if(queue == null){
            queue = new LinkedList<>();
        }
        */
        prefs = new MyPreferences(this);
        if (prefs.getDoNotDisturb()) {
            GCMBroadcastReceiver.completeWakefulIntent(intent);
            return;
        }
        Bundle extras = intent.getExtras();
        try {
            AccidentsGeneral.points.addPoint(new Accident(extras, this));
        } catch (Accident.MCPointException e) {
            e.printStackTrace();
        }
        String type      = extras.getString("type");
        String message   = extras.getString("message");
        String title     = extras.getString("title");
        String idString  = extras.getString("id");
        String latString = extras.getString("lat");
        String lngString = extras.getString("lon");
        if (idString.equals(null)) return;
        extras.putInt("toDetails", Integer.parseInt(idString));
        int    id;
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

        Resources            res     = this.getResources();
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
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        manageTray(id);
        GCMBroadcastReceiver.completeWakefulIntent(intent);
        /*
        queue.add(id);
        if(queue.size() > prefs.getMaxNotifications()){
            removeNotification(queue.poll());
        }
        */
    }

    private void manageTray(int id) {
        JSONArray tray = prefs.getNotificationList();
        tray.put(String.valueOf(id));
        int max = prefs.getMaxNotifications();
        if (max < tray.length()) {
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
        }
        prefs.setNotificationList(tray);
    }
    public static void clearAll(){
        MyPreferences.setNotificationList(new JSONArray());
        notificationManager.cancelAll();
    }
    /*
    public static void removeNotification(int id){
        try {
            notificationManager.cancel(id);
        }catch (Exception e){
        }
    }
    */
    /*
    public static void clearQueue(){
        if(queue != null){
            queue.clear();
        }
    }
    */
}
