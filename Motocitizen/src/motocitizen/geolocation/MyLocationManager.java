package motocitizen.geolocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import motocitizen.MyApp;

public class MyLocationManager {

    public static boolean permissionRequested = false;

    private MyLocationManager() {
    }

    private static class Holder {
        private static SecuredLocationManagerInterface instance;
    }

    public static SecuredLocationManagerInterface getInstance() {
        if (Holder.instance == null) {
            setProvider();
        }
        if (Holder.instance instanceof FakeLocationManager && permissionGranted()) {
            setProvider();
        }
        if (Holder.instance instanceof NormalLocationManager && !permissionGranted()) {
            setProvider();
        }
        return Holder.instance;
    }

    private static void setProvider() {
        if (permissionGranted()) Holder.instance = new NormalLocationManager();
        else Holder.instance = new FakeLocationManager();
    }

    private static boolean permissionGranted() {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (ContextCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        if (!permissionRequested) {
            MyApp.getCurrentActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MyApp.LOCATION_PERMISSION);
            permissionRequested = true;
        }
        return ContextCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
