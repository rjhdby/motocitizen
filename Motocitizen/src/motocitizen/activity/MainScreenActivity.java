package motocitizen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import motocitizen.MyApp;
import motocitizen.fragments.MainScreenFragment;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.utils.ChangeLog;
import motocitizen.utils.Preferences;

public class MainScreenActivity extends AppCompatActivity {

    private static ActionBar          actionBar;
    private        MainScreenFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);
        actionBar = getSupportActionBar();
        mainFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mainFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLocationManager.getInstance().wakeup(this);

        if (Preferences.getInstance().isNewVersion()) {
            AlertDialog changeLogDlg = ChangeLog.getDialog(this);
            changeLogDlg.show();
            Preferences.getInstance().resetNewVersion();
        }
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            this.findViewById(R.id.dial_button).setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLocationManager.getInstance().sleep(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("toMap")) {
            mainFragment.toMap(intent.getExtras().getInt("toMap", 0));
            intent.removeExtra("toMap");
        }
        if (intent.hasExtra("toDetails")) {
            intent.removeExtra("toDetails");
        }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;
        switch (requestCode) {
            case MyApp.LOCATION_PERMISSION:
                if (this.checkSelfPermission(Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED)
                    MyLocationManager.getInstance();
                MyLocationManager.permissionRequested = false;
                break;
        }
    }
}
