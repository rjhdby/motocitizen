package motocitizen.geolocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import motocitizen.MyApp;

public class MyLocationManager {

    public static boolean permissionRequested = false;

    private MyLocationManager() {
    }

    private static class Holder {
        private static SecuredLocationManagerInterface instance = new FakeLocationManager();
    }

    public static void init(Context context) {
        if (permissionGranted(context)) Holder.instance = new NormalLocationManager();
        else Holder.instance = new FakeLocationManager();
    }

    public static SecuredLocationManagerInterface getInstance() {
        return Holder.instance;
    }

    private static boolean permissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        if (!permissionRequested) {
            ((Activity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MyApp.LOCATION_PERMISSION);
            permissionRequested = true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
