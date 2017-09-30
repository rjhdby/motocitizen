package motocitizen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import motocitizen.datasources.database.Database;
import motocitizen.datasources.preferences.Preferences;
import motocitizen.geo.MyGoogleApiClient;
import motocitizen.geo.geocoder.MyGeoCoder;
import motocitizen.ui.activity.AuthActivity;
import motocitizen.user.Auth;
import motocitizen.utils.GraphUtils;

public class MyApp extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void logoff() {
        Auth.INSTANCE.logoff();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.INSTANCE.initialize(this);
        Database.INSTANCE.initialize(this);
        GraphUtils.INSTANCE.initialize(this);
        MyGeoCoder.INSTANCE.initialize(this);
        MyGoogleApiClient.INSTANCE.initialize(this);
        FirebaseMessaging.getInstance().subscribeToTopic("accidents");
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

    /**
     * AccessToken invalidated. Слушатель токена
     */
    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Toast.makeText(getApplicationContext(), "Авторизация слетела, авторизируйтесь снова", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };
}
