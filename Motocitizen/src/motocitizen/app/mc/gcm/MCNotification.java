package motocitizen.app.mc.gcm;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

public class MCNotification {
	// private static int NOTIFY_ID;

	public MCNotification(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		String type = extras.getString("type");
		String message = extras.getString("message");
		String title = extras.getString("title");
		int id;
		try {
			id = Integer.valueOf(extras.getString("id"));
		} catch (Exception e) {
			id = MCAccidents.currentPoint.id;
		}

		Intent notificationIntent = new Intent(context, Startup.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtras(extras);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Resources res = context.getResources();
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.logo).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
				.setTicker(title).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentText(message);
		SharedPreferences prefs = context.getSharedPreferences("motocitizen.startup", Context.MODE_PRIVATE);
		if (type.equals("acc")) {
			prefs.edit().putInt("mc.show.details", id).commit();
		}
		@SuppressWarnings("deprecation")
		Notification notification = builder.getNotification();
		String sound = Startup.prefs.getString("mc.notification.sound", "default system");
		if (sound.equals("default system")) {
			notification.defaults = Notification.DEFAULT_ALL;
			// notification.defaults = Notification.DEFAULT_VIBRATE;
		} else {
			notification.vibrate = new long[] { 1000, 1000, 1000 };
			notification.sound = Uri.parse("file://" + sound);
		}
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id, notification);
	}
}
