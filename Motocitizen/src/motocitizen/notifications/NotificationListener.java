package motocitizen.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationListener extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Map    data = remoteMessage.getData();
        Log.d("NOTIFICATION", from);
    }
}
