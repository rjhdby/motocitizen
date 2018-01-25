package motocitizen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.MyGoogleApiClient
import motocitizen.migration.Migration
import motocitizen.ui.activity.AuthActivity
import motocitizen.user.Auth

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
        Migration.makeMigration(this)

        context = applicationContext

        Preferences.initialize(this)
        MyGoogleApiClient.initialize(this)
        FirebaseMessaging.getInstance().subscribeToTopic("accidents")
        if (Preferences.isTester) FirebaseMessaging.getInstance().subscribeToTopic("test")

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

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
