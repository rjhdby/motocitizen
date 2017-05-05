package motocitizen.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;

import motocitizen.database.DbOpenHelper;
import motocitizen.geocoder.MyGeoCoder;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.user.User;

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
        DbOpenHelper.init(this);
        MyGeoCoder.init(this);
        FirebaseMessaging.getInstance().subscribeToTopic("accidents");
        Dexter.withActivity(this)
              .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(new BasePermissionListener() {
                  @Override
                  public void onPermissionGranted(PermissionGrantedResponse response) {
                      MyLocationManager.enableReal();
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
                      ahead();
                  }
              }).check();
    }

    private void ahead() {
        Router.INSTANCE.goTo(this, User.getInstance(this).isAuthorized() ? Router.Target.MAIN : Router.Target.AUTH);
    }
}
