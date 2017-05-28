package motocitizen;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import motocitizen.user.User;

public class MyApp extends MultiDexApplication {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



    public static void logoff() {
        User.dirtyRead().logoff();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }



    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Toast.makeText(getApplicationContext(), "dfdfd",Toast.LENGTH_LONG);
// VKAccessToken is invalid
            }
        }
    };

}
