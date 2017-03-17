package motocitizen.notifications;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class TokenReceiver extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
//        super.onTokenRefresh();
        Log.d("TOKEN", FirebaseInstanceId.getInstance().getToken());
    }
}
