package motocitizen.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import motocitizen.content.Content;
import motocitizen.database.DbOpenHelper;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geocoder.MyGeocoder;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.user.Auth;
import motocitizen.utils.Preferences;

public class StartupActivity extends AppCompatActivity {
    private ProgressBar startupProgress;
    private TextView    startupLog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

    }

    @Override
    public void onResume() {
        super.onResume();
        startupProgress = (ProgressBar) findViewById(R.id.startupProgress);
        startupLog = (TextView) findViewById(R.id.startupLog);
        startupProgress.setMax(6);

        new Thread(() -> {
            iterate(() -> Preferences.init(getBaseContext()), "Загрузка настроек", "Готово");
            iterate(() -> DbOpenHelper.init(getBaseContext()), "Инициализация базы данных", "Готово");

            //todo check permissions
            iterate(() -> {
                MyLocationManager.init(getBaseContext());
                MyGeocoder.init(getBaseContext());
            }, "Инициализация геопозиционирования", "Готово");

            iterate(Auth::init, "Авторизация");
            echoLn(Auth.getInstance().isAuthorized() ? Auth.getInstance().getRole().getName() : "Не авторизован");

            iterate(Content::init, "Подготовка контента", "Готово");

            //todo check permissions
            iterate(() -> {
                new GCMRegistration(getBaseContext());
                new GCMBroadcastReceiver();
            }, "Инициализация оповещений", "Готово");

            //todo route to auth
            Router.goTo(this, Auth.getInstance().isAuthorized() ? Router.Target.MAIN : Router.Target.AUTH);
        }).start();
    }

    private void iterate(Runnable step, String prefix, String suffix) {
        iterate(step, prefix);
        echoLn(suffix);
    }

    private void iterate(Runnable step, String prefix) {
        echo(prefix + "... ");
        SystemClock.sleep(1000);
        handler.post(step);
        handler.post(() -> startupProgress.setProgress(startupProgress.getProgress() + 1));
    }

    private void echo(String text) {
        handler.post(() -> startupLog.append(text));
    }

    private void echoLn(String text) {
        handler.post(() -> startupLog.append(text + "\n"));
    }
}
