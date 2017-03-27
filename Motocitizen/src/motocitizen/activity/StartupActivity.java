package motocitizen.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.EmptyPermissionListener;

import motocitizen.content.Content;
import motocitizen.database.DbOpenHelper;
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
        if (FirebaseInstanceId.getInstance().getToken() != null) {
            Log.e("TOKEN", FirebaseInstanceId.getInstance().getToken());
        }
        Preferences.init(this);
        DbOpenHelper.init(this);
        MyGeoCoder.init(this);
        User.init();
        Content.init();
        FirebaseMessaging.getInstance().subscribeToTopic("accidents");
        Dexter.withActivity(this)
              .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(new EmptyPermissionListener() {
                  @Override
                  public void onPermissionGranted(PermissionGrantedResponse response) {
                      MyLocationManager.init(true);
                      ahead();
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                      super.onPermissionRationaleShouldBeShown(permission, token);
                      token.continuePermissionRequest();
                  }

                  @Override
                  public void onPermissionDenied(PermissionDeniedResponse response) {
                      super.onPermissionDenied(response);
                      MyLocationManager.init(false);
                      ahead();
                  }
              }).check();
    }

    private void ahead() {
        Router.goTo(this, User.getInstance().isAuthorized() ? Router.Target.MAIN : Router.Target.AUTH);
    }
}
