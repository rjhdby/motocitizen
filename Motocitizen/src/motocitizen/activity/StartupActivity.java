package motocitizen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import motocitizen.content.Content;
import motocitizen.database.DbOpenHelper;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geocoder.MyGeoCoder;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_startup);
    }

    //TODO проверка разрешений
    @Override
    public void onResume() {
        super.onResume();
        Preferences.init(this);
        DbOpenHelper.init(this);
        MyLocationManager.init(this);
        MyGeoCoder.init(this);
        User.init();
        Content.init();
        new GCMRegistration(this);
        new GCMBroadcastReceiver();
        Router.goTo(this, User.getInstance().isAuthorized() ? Router.Target.MAIN : Router.Target.AUTH);
    }
}
