package motocitizen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.widget.Toast
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import motocitizen.datasources.preferences.Preferences
import motocitizen.migration.Migration
import motocitizen.notifications.Messaging
import motocitizen.ui.activity.AuthActivity

class MyApp : Application() {


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
            } else {
                Preferences.vkToken = newToken.accessToken
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        oldVersion = PreferenceManager.getDefaultSharedPreferences(this).getInt("mc.app.version", 0)
        val currentVersion = packageManager.getPackageInfo(packageName, 0).versionCode
        if (oldVersion < currentVersion) Migration.makeMigration(this)

        Messaging.subscribe()
        if (Preferences.isTester) Messaging.subscribeToTest()

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var firstStart = false
        var oldVersion = 0

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
