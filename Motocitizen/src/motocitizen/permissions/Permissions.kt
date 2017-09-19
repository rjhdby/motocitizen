package motocitizen.permissions

import android.Manifest
import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.BasePermissionListener
import motocitizen.geo.geolocation.MyLocationManager

object Permissions {
    fun requestLocation(activity: Activity, successCallback: () -> Unit, failureCallback: () -> Unit = {}) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(permissionListener(successCallback, failureCallback))
                .check()
    }

    private fun permissionListener(successCallback: () -> Unit, failureCallback: () -> Unit): BasePermissionListener {
        return object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                MyLocationManager.enableReal()
                successCallback()
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken) {
                super.onPermissionRationaleShouldBeShown(permission, token)
                token.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                super.onPermissionDenied(response)
                failureCallback()
            }
        }
    }
}