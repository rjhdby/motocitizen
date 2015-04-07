package motocitizen.startup;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import motocitizen.Activity.AuthActivity;
import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCLocation;
import motocitizen.app.mc.gcm.GcmBroadcastReceiver;
// zz
// import motocitizen.core.settings.SettingsMenu;
import motocitizen.main.R;
import motocitizen.maps.general.MCMap;
import motocitizen.maps.google.MCGoogleMap;
import motocitizen.maps.osm.MCOSMMap;
import motocitizen.network.IncidentRequest;
import motocitizen.network.JsonRequest;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.MCUtils;
import motocitizen.utils.Props;
import motocitizen.utils.Show;

public class Startup extends FragmentActivity {
    public static Props props;
    public static Context context;
    public static SharedPreferences prefs;
    public static MCMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        context = this;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        new Const();

        //prefs = getSharedPreferences("motocitizen.startup", MODE_PRIVATE);
        //prefs.edit().clear().commit();
        props = new Props();

        new MCAccidents(this, prefs);

        createMap(prefs.getString("map_pref", MCMap.OSM));
        // zz
        // new SettingsMenu();
        new SmallSettingsMenu();
        if (MCAccidents.auth.isFirstRun()) {
            //Show.show(R.id.main_frame, R.id.first_auth_screen);
            Intent i = new Intent(Startup.context, AuthActivity.class);
            Startup.context.startActivity(i);
        } else {
//            Show.show(R.id.main_frame, R.id.main_screen_fragment);
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

//        Show.show(R.id.main_frame, R.id.main_screen_fragment);
        MCLocation.wakeup(this);
        Intent intent = getIntent();
        context = this;
        //MCAccidents.refresh(this);

        if (isOnline()) {
            JsonRequest request = MCAccidents.getLoadPointsRequest();
            if (request != null) {
                (new IncidentRequest()).execute(request);
            }
            catchIntent(intent);
        } else {
            Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
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
                FragmentManager fm = getFragmentManager();
                Fragment pf = fm.findFragmentByTag("settings");
                if(pf != null && pf.isVisible()){
                    Fragment mf = fm.findFragmentByTag("main_screen");
                    fm.beginTransaction().show(mf).hide(pf).commit();
                    MCAccidents.redraw(this);
                }else{
                    Show.showLast();
                }
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

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Startup.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void createMap(String name) {
        if (map != null && !map.getName().equals(name))
            map = null;

        if (name.equals(MCMap.OSM)) {
            map = new MCOSMMap(context);
        } else if (name.equals(MCMap.GOOGLE)) {
            map = new MCGoogleMap(context);
        }
        map.jumpToPoint(MCLocation.current);
    }
}
