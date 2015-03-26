package motocitizen.startup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Window;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCLocation;
import motocitizen.app.mc.gcm.GcmBroadcastReceiver;
import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.maps.general.MCMap;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.MCUtils;
import motocitizen.utils.Props;
import motocitizen.utils.Show;

public class Startup extends Activity {
    public static Props props;
    public static Context context;
    public static SharedPreferences prefs;
    private SharedPreferences prefsDef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        context = this;

        prefsDef = PreferenceManager.getDefaultSharedPreferences(this);

        new Const();

        prefs = getSharedPreferences("motocitizen.startup", MODE_PRIVATE);
        // prefs.edit().clear().commit();
        props = new Props();
        new MCAccidents(this, prefsDef);
        new MCMap(this);
        new SettingsMenu();
        new SmallSettingsMenu();
        if (MCAccidents.auth.isFirstRun()) {
            Show.show(R.id.main_frame, R.id.first_auth_screen);
        } else {
            Show.show(R.id.main_frame, R.id.main_frame_applications);
        }
        new GcmBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MCLocation.sleep();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MCLocation.wakeup(this);
        Intent intent = getIntent();
        context = this;
        MCAccidents.refresh(this);
        catchIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                SmallSettingsMenu.popupBL.show();
                Keyboard.hide();
                return true;
            case KeyEvent.KEYCODE_BACK:
                Show.showLast();
                Keyboard.hide();
                return true;
        }
        return super.onKeyUp(keycode, e);
    }

    private void catchIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        String type = extras.getString("type");
        String idString = extras.getString("id");
        if (type == null || idString == null || !MCUtils.isInteger(idString)) {
            return;
        }
        int id = Integer.parseInt(idString);
        if (type.equals("acc") && id != 0) {
            MCAccidents.points.setSelected(this, id);
            MCAccidents.toDetails(this, id);
        }
    }
}
