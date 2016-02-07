package motocitizen.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import motocitizen.MyApp;
import motocitizen.fragments.MainScreenFragment;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.utils.ChangeLog;

public class MainScreenActivity extends ActionBarActivity {

    private static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);
        MyApp.setCurrentActivity(this);
        actionBar = getSupportActionBar();
    }

    private void initSecuredComponents() {
        MyApp.geocoder = new Geocoder(this);
        new GCMRegistration();
        new GCMBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.setCurrentActivity(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MainScreenFragment()).commit();
        MyLocationManager.getInstance().wakeup();

        if (ChangeLog.isNewVersion()) {
            AlertDialog changeLogDlg = ChangeLog.getDialog();
            changeLogDlg.show();
        }
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            this.findViewById(R.id.dial_button).setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLocationManager.getInstance().sleep();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public static void updateStatusBar(String address) {
        String subTitle = "";
        //Делим примерно пополам, учитывая пробел или запятую
        int commaPos = address.lastIndexOf(",", address.length() / 2);
        int spacePos = address.lastIndexOf(" ", address.length() / 2);

        if (commaPos != -1 || spacePos != -1) {
            subTitle = address.substring(Math.max(commaPos, spacePos) + 1);
            address = address.substring(0, Math.max(commaPos, spacePos));
        }

        actionBar.setTitle(address);
        if (!subTitle.isEmpty()) actionBar.setSubtitle(subTitle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;
        switch (requestCode) {
            case MyApp.LOCATION_PERMISSION:
                if (this.checkSelfPermission(Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED)
                    MyLocationManager.getInstance();
                MyLocationManager.permissionRequested = false;
                break;
            default:
                initSecuredComponents();
        }
    }
}
