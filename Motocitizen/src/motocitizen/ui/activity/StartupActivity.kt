package motocitizen.ui.activity

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.BasePermissionListener
import motocitizen.geolocation.MyLocationManager
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.user.User
import motocitizen.datasources.preferences.Preferences

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
        setContentView(R.layout.activity_startup)
    }

    //TODO проверка разрешений
    public override fun onResume() {
        super.onResume()
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(permissionListener())
                .check()
    }

    private fun permissionListener(): BasePermissionListener {
        return object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                MyLocationManager.enableReal()
                ahead()
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken) {
                super.onPermissionRationaleShouldBeShown(permission, token)
                token.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                super.onPermissionDenied(response)
                ahead()
            }
        }
    }

    private fun ahead() {
        if (Preferences.anonymous) {
            Router.goTo(this, Router.Target.MAIN)
            return
        }
        if (Preferences.login == "") {
            Router.goTo(this, Router.Target.AUTH)
            return
        }
        User.auth(
                Preferences.login,
                Preferences.password,
                { Router.goTo(this@StartupActivity, if (User.isAuthorized) Router.Target.MAIN else Router.Target.AUTH) })
    }
}
