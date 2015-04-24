package motocitizen.startup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import motocitizen.Activity.AboutActivity;
import motocitizen.Activity.AuthActivity;
import motocitizen.Activity.CreateAccActivity;
import motocitizen.Activity.SettingsActivity;
import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCLocation;
import motocitizen.app.mc.gcm.GcmBroadcastReceiver;
import motocitizen.app.mc.user.MCRole;
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

import java.lang.*;

public class Startup extends ActionBarActivity implements View.OnClickListener {
    public static Props props;
    public static Context context;
    public static MCPreferences prefs;
    public static MCMap map;
    public static boolean fromDetails;

    private ImageButton dialButton;
    private ImageButton createAccButton;

    private RadioGroup mainTabsGroup;

    private View accListView;
    private View mapContainer;

    private Menu mMenu;

    private static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        context = this;

        actionBar = getSupportActionBar();

        //prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = new MCPreferences(this);
        prefs.setDoNotDistrub(false);
        new Const();

        checkUpdate();

        dialButton = (ImageButton) findViewById(R.id.dial_button);
        dialButton.setOnClickListener(this);

        createAccButton = (ImageButton) findViewById(R.id.mc_add_point_button);
        createAccButton.setOnClickListener(this);

        mainTabsGroup = (RadioGroup) findViewById(R.id.main_tabs_group);
        mainTabsGroup.setOnCheckedChangeListener(mainTabsListener);

        accListView = findViewById(R.id.mc_acc_list);
        mapContainer = findViewById(R.id.map_container);
        mapContainer.setTranslationX(Const.getWidth(context));

        //prefs = getSharedPreferences("motocitizen.startup", MODE_PRIVATE);
        //prefs.edit().clear().commit();
        props = new Props();

        new MCAccidents(this);

        createMap(prefs.getMapProvider());
        // zz
        // new SettingsMenu();
        if (MCAccidents.auth.isFirstRun()) {
            //Show.show(R.id.main_frame, R.id.first_auth_screen);
            Intent i = new Intent(Startup.context, AuthActivity.class);
            Startup.context.startActivity(i);
        }
        new GcmBroadcastReceiver();
    }

    private void checkUpdate(){
        PackageManager manager = this.getPackageManager();
        String version;
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = getString(R.string.unknown_code_version);
        }
        if(!prefs.getCurrentVersion().equals(version)){
            ChangeLog.getDialog(this, true).show();
        }
        prefs.setCurrentVersion(version);
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
        Integer toMap = intent.getIntExtra("toMap", 0);
        Integer toDetails = intent.getIntExtra("toDetails", 0);
        context = this;
        //MCAccidents.refresh(this);

        if (MCRole.isStandart()) {
            createAccButton.setVisibility(View.VISIBLE);
        } else {
            createAccButton.setVisibility(View.INVISIBLE);
        }

        if (isOnline()) {
            JsonRequest request = MCAccidents.getLoadPointsRequest();
            if (request != null) {
                (new IncidentRequest(this)).execute(request);
            }
            catchIntent(intent);
        } else {
            Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
        if(toMap != 0){
            intent.removeExtra("toMap");
            mainTabsGroup.check(R.id.tab_map_button);
            fromDetails = intent.getBooleanExtra("fromDetails", false);
        } else if(toDetails != 0){
            intent.removeExtra("toDetails");
            MCAccidents.refresh(this);
            MCAccidents.toDetails(this, toDetails);
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
            case KeyEvent.KEYCODE_BACK:
                if(fromDetails){
                    MCAccidents.toDetails(this);
                }
//                FragmentManager fm = getFragmentManager();
//                Fragment pf = fm.findFragmentByTag("settings");
//                if(pf != null && pf.isVisible()){
//                    Fragment mf = fm.findFragmentByTag("main_screen");
//                    fm.beginTransaction().show(mf).hide(pf).commit();
                    MCAccidents.redraw(this);
//                }
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

        Location location = MCLocation.getLocation(context);
        //if(location != null)
            map.jumpToPoint(location);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.dial_button:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+74957447350"));
                Startup.context.startActivity(intent);
                break;
            case R.id.mc_add_point_button:
                startActivity(new Intent(Startup.context, CreateAccActivity.class));
                break;
            default:
                Log.e("Startup", "Unknown button pressed");
                break;
        }
    }

    private final RadioGroup.OnCheckedChangeListener mainTabsListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            fromDetails = false;
            accListView.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.VISIBLE);

            if (Show.currentGeneral == null) {
                Show.currentGeneral = R.id.tab_accidents_button;
            }

            if (id == R.id.tab_accidents_button) {
                accListView.animate().translationX(0);
                mapContainer.animate().translationX(Const.getWidth(context) * 2);
            } else if (id == R.id.tab_map_button) {
                accListView.animate().translationX(-Const.getWidth(context) * 2);
                mapContainer.animate().translationX(0);
            }
            Show.currentGeneral = id;
        }
    };

    public static void updateStatusBar(String address) {

        String subTitle = "";
        //Делим примерно пополам, учитывая пробел или запятую
        int commaPos = address.lastIndexOf(",", address.length() / 2);
        int spacePos = address.lastIndexOf(" ", address.length() / 2);

        if(commaPos != -1 || spacePos != -1) {
            subTitle = address.substring(Math.max(commaPos, spacePos) + 1);
            address = address.substring(0,Math.max(commaPos, spacePos));
        }

        actionBar.setTitle(address);
        if(!subTitle.isEmpty())
            actionBar.setSubtitle(subTitle);
//        Text.set(context, R.id.statusBarText, name + address);
        map.placeUser(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.small_settings_menu, menu);
        mMenu = menu;

        MenuItem itemMenuNotDistrub = mMenu.findItem(R.id.do_not_distrub);

        if(prefs.getDoNotDistrub())
            itemMenuNotDistrub.setIcon(R.drawable.ic_lock_ringer_on_alpha);
        else
            itemMenuNotDistrub.setIcon(R.drawable.ic_lock_ringer_off_alpha);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.small_menu_refresh:
                MCAccidents.refresh(context);
                if (Startup.isOnline()) {
                    JsonRequest request = MCAccidents.getLoadPointsRequest();
                    if (request != null) {
                        (new IncidentRequest(context)).execute(request);
                    }
                } else {
                    Toast.makeText(context, Startup.context.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.small_menu_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                Startup.context.startActivity(intentSettings);
                return true;
            case R.id.small_menu_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                context.startActivity(intentAbout);
                return true;
            case R.id.small_menu_exit:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(intent);
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                return true;
            case R.id.do_not_distrub:
                MCPreferences prefs = new MCPreferences(Startup.context);
                MenuItem menuItemActionDistrub = mMenu.findItem(R.id.do_not_distrub);
                if(prefs.getDoNotDistrub()){
                    item.setIcon(R.drawable.ic_lock_ringer_on_alpha);
                    prefs.setDoNotDistrub(false);
                } else {
                    item.setIcon(R.drawable.ic_lock_ringer_off_alpha);
                    prefs.setDoNotDistrub(true);
                }
                return true;
        }
        return false;
    }
}
