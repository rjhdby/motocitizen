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
import android.os.Bundle;
import android.util.Log;

public class MCNotification {
//	private static int NOTIFY_ID;

	public MCNotification(String title, String text, int id) {
//		if ((Integer) NOTIFY_ID == null) {
//			NOTIFY_ID = 0;
//		}
		Context context = Startup.context;
		//Intent notificationIntent = new Intent(Startup.context, AuthorizedUser.class);
		Intent notificationIntent = new Intent(Startup.context, Startup.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Resources res = context.getResources();
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.logo)
				.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
				.setTicker(text)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setContentTitle(title).setContentText(text);

		@SuppressWarnings("deprecation")
		Notification notification = builder.getNotification ();
		String sound = Startup.prefs.getString("mc.notification.sound", "default system");
		Log.d("PREF SOUND", sound);
		if (sound.equals("default system")) {
			notification.defaults = Notification.DEFAULT_ALL;
			//notification.defaults = Notification.DEFAULT_VIBRATE;
		} else {
			notification.vibrate = new long[] { 1000, 1000, 1000 };
			notification.sound = Uri.parse("file://" + sound);
		}
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id, notification);
	}
	private PendingIntent getPendingIntent(Bundle bundle, int id) { 
		Intent notificationIntent = new Intent(Startup.context, Startup.class);
		notificationIntent.putExtras(bundle);
		return PendingIntent.getActivity(Startup.context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
}
