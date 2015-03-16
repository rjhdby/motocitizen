package motocitizen.app.mc.gcm;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import motocitizen.utils.Utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

public class MCNotification {
	@SuppressWarnings("deprecation")
	public MCNotification(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		//String type = extras.getString("type");
		String message = extras.getString("message");
		String title = extras.getString("title");
		String idString = extras.getString("id");
		int id;
		if (Utils.isInteger(idString)) {
			id = Integer.valueOf(idString);
		} else {
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

		String sound = context.getSharedPreferences("motocitizen.startup", Context.MODE_PRIVATE).getString("mc.notification.sound", "default system");
		if (sound.equals("default system")) {
			builder.setDefaults(Notification.DEFAULT_ALL);
		} else {
			builder.setSound(Uri.parse(sound), AudioManager.STREAM_NOTIFICATION);
			builder.setVibrate(new long[] { 1000, 1000, 1000 });
		}
		Notification notification = builder.getNotification();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id, notification);
	}
}
