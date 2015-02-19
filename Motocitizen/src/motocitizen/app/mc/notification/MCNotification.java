package motocitizen.app.mc.notification;

import motocitizen.main.R;
import motocitizen.startup.Startup;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class MCNotification {
	private static int NOTIFY_ID;

	public MCNotification(String text) {
		if ((Integer) NOTIFY_ID == null) {
			NOTIFY_ID = 0;
		}
		Context context = Startup.context;
		Intent notificationIntent = new Intent(Startup.context, Startup.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Resources res = context.getResources();
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.phone)
				.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.phone))
				.setTicker("Последнее китайское предупреждение!")
				.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setContentTitle("Напоминание").setContentText(text);

		@SuppressWarnings("deprecation")
		Notification notification = builder.getNotification ();
		/*
		 * Notification notification = new Notification(); notification.icon =
		 * R.drawable.phone; notification.largeIcon =
		 * BitmapFactory.decodeResource(res, R.drawable.phone);
		 * notification.tickerText = "text"; notification.when =
		 * System.currentTimeMillis();
		 */
		String sound = Startup.prefs.getString("mc.notification.sound", "default system");
		Log.d("PREF SOUND", sound);
		if (sound.equals("default system")) {
			notification.defaults = Notification.DEFAULT_ALL;
		} else {
			notification.vibrate = new long[] { 1000, 1000, 1000 };
			notification.sound = Uri.parse("file://" + sound);
		}
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFY_ID++, notification);
	}
}
