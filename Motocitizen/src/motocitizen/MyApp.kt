package motocitizen

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.widget.Toast

import com.google.firebase.messaging.FirebaseMessaging
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk

import motocitizen.datasources.database.Database
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.MyGoogleApiClient
import motocitizen.geo.geocoder.MyGeoCoder
import motocitizen.ui.activity.AuthActivity
import motocitizen.user.Auth
import motocitizen.utils.GraphUtils

class MyApp : MultiDexApplication() {

    /**
     * AccessToken invalidated. Слушатель токена
     */
    private var vkAccessTokenTracker: VKAccessTokenTracker = object : VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {
                Toast.makeText(applicationContext, "Авторизация слетела, авторизируйтесь снова", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Preferences.initialize(this)
        Database.initialize(this)
        GraphUtils.initialize(this)
        MyGeoCoder.initialize(this)
        MyGoogleApiClient.initialize(this)
        FirebaseMessaging.getInstance().subscribeToTopic("accidents")
        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(this)
    }

    companion object {

        fun logoff() {
            Auth.logoff()
        }

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
