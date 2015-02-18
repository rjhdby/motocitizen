package motocitizen.app.mc.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import motocitizen.main.R;
import motocitizen.startup.Startup;

@SuppressLint("NewApi")
public class MCNotification {
	private static int NOTIFY_ID;
	public MCNotification(String text){
		if((Integer) NOTIFY_ID == null){
			NOTIFY_ID = 0;
		}
		Activity act = (Activity) Startup.context;
		Context context = Startup.context;
		Intent notificationIntent = new Intent(Startup.context, Startup.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.phone)
        // большая картинка
        .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.phone))
        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
        .setTicker("Последнее китайское предупреждение!")
        .setWhen(System.currentTimeMillis())
        .setAutoCancel(true)
        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
        .setContentTitle("Напоминание")
        //.setContentText(res.getString(R.string.notifytext))
        .setContentText(text); // Текст уведомленимя
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;
        
        //Uri ringURI =               RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //notification.sound = ringURI;
        //notification.sound = Uri.parse("file:///sdcard/cat.mp3");
        
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);        
        notificationManager.notify(NOTIFY_ID++, notification);
	}
}
