package motocitizen.app.mc.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new MCNewAccReceived(context, intent);
        setResultCode(Activity.RESULT_OK);
    }
}