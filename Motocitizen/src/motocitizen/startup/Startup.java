package motocitizen.startup;

import android.app.AlertDialog;
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
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.Activity.AboutActivity;
import motocitizen.Activity.CreateAccActivity;
import motocitizen.Activity.SettingsActivity;
import motocitizen.MyApp;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.MyLocationManager;
import motocitizen.app.general.gcm.GCMBroadcastReceiver;
import motocitizen.app.general.gcm.NewAccidentReceived;
import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.maps.general.MyMapManager;
import motocitizen.maps.google.MyGoogleMapManager;
import motocitizen.maps.osm.MyOSMMapManager;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.utils.Const;
import motocitizen.utils.RefreshAnimation;

public class Startup extends ActionBarActivity implements View.OnClickListener {

    private MyApp myApp = null;
    public static Context       context;
    public static MyPreferences prefs;
    public static MyMapManager  map;
    public static boolean       fromDetails;

    private ImageButton createAccButton;

    private RadioGroup mainTabsGroup;

    private View       accListView;
    private View       mapContainer;

    public static Menu mMenu;

    private static ActionBar actionBar;
    private static AlertDialog changeLogDlg = null;

    public static Integer          currentGeneral;
    public static RefreshAnimation refreshAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplicationContext();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.main);
        context = this;
        actionBar = getSupportActionBar();

        prefs = myApp.getPreferences();
        prefs.setDoNotDisturb(false);
        new Const();

        checkUpdate();

        ImageButton dialButton = (ImageButton) findViewById(R.id.dial_button);
        dialButton.setOnClickListener(this);

        createAccButton = (ImageButton) findViewById(R.id.mc_add_point_button);
        createAccButton.setOnClickListener(this);

        mainTabsGroup = (RadioGroup) findViewById(R.id.main_tabs_group);
        mainTabsGroup.setOnCheckedChangeListener(mainTabsListener);

        accListView = findViewById(R.id.mc_acc_list);
        mapContainer = findViewById(R.id.map_container);
        mapContainer.setTranslationX(Const.getWidth(context));

        accListView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                return true;
            }
        });

        new AccidentsGeneral(this);

        createMap(MyMapManager.GOOGLE);
        new GCMBroadcastReceiver();
    }

    private void checkUpdate() {
        PackageManager manager = this.getPackageManager();
        String         version;
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = getString(R.string.unknown_code_version);
        }
        if (!prefs.getCurrentVersion().equals(version)) {
            changeLogDlg = ChangeLog.getDialog(this, true);
            changeLogDlg.show();
        }
        prefs.setCurrentVersion(version);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLocationManager.sleep();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLocationManager.wakeup();
        Intent  intent    = getIntent();
        Integer toMap     = intent.getIntExtra("toMap", 0);
        Integer toDetails = intent.getIntExtra("toDetails", 0);
        context = this;

        if (Role.isStandart()) {
            createAccButton.setVisibility(View.VISIBLE);
        } else {
            createAccButton.setVisibility(View.INVISIBLE);
        }

        getAccidents(true);
        if (toMap != 0) {
            intent.removeExtra("toMap");
            mainTabsGroup.check(R.id.tab_map_button);
            fromDetails = intent.getBooleanExtra("fromDetails", false);
        } else if (toDetails != 0) {
            intent.removeExtra("toDetails");
            AccidentsGeneral.refresh(this);
            AccidentsGeneral.toDetails(this, toDetails);
            NewAccidentReceived.clearAll(this);
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
                if (fromDetails) {
                    AccidentsGeneral.toDetails(this);
                }
                AccidentsGeneral.redraw(this);
                return true;
        }
        return super.onKeyUp(keycode, e);
    }
    public static boolean isOnline() {
        ConnectivityManager cm      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void createMap(String name) {
        if (map != null && !map.getName().equals(name))
            map = null;

        if (name.equals(MyMapManager.OSM)) {
            map = new MyOSMMapManager(context);
        } else if (name.equals(MyMapManager.GOOGLE)) {
            map = new MyGoogleMapManager(context);
        }

        Location location = MyLocationManager.getLocation(context);
        map.jumpToPoint(location);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.dial_button:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+78007751734"));
                this.startActivity(intent);
                break;
            case R.id.mc_add_point_button:
                startActivity(new Intent(this, CreateAccActivity.class));
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

            if (currentGeneral == null) {
                currentGeneral = R.id.tab_accidents_button;
            }

            if (id == R.id.tab_accidents_button) {
                accListView.animate().translationX(0);
                mapContainer.animate().translationX(Const.getWidth(context) * 2);
            } else if (id == R.id.tab_map_button) {
                accListView.animate().translationX(-Const.getWidth(context) * 2);
                mapContainer.animate().translationX(0);
            }
            currentGeneral = id;
        }
    };

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
        if (!subTitle.isEmpty())
            actionBar.setSubtitle(subTitle);
        map.placeUser(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.small_settings_menu, menu);
        mMenu = menu;
        MenuItem itemMenuNotDisturb = mMenu.findItem(R.id.do_not_disturb);
        MenuItem refreshItem        = mMenu.findItem(R.id.action_refresh);
        refreshAnimation = new RefreshAnimation(refreshItem);
        refreshAnimation.onRefreshBeginning();
        if (prefs.getDoNotDisturb())
            itemMenuNotDisturb.setIcon(R.drawable.ic_lock_ringer_off_alpha);
        else
            itemMenuNotDisturb.setIcon(R.drawable.ic_lock_ringer_on_alpha);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.small_menu_refresh:
                // Do animation start
                getAccidents();
                return true;
            case R.id.small_menu_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                this.startActivity(intentSettings);
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
            case R.id.action_refresh:
                getAccidents();
                return true;
            case R.id.do_not_disturb:
                MyPreferences prefs = myApp.getPreferences();
                //MenuItem menuItemActionDisturb = mMenu.findItem(R.id.do_not_disturb);
                if (prefs.getDoNotDisturb()) {
                    item.setIcon(R.drawable.ic_lock_ringer_on_alpha);
                    prefs.setDoNotDisturb(false);
                } else {
                    item.setIcon(R.drawable.ic_lock_ringer_off_alpha);
                    prefs.setDoNotDisturb(true);
                }
                return true;
        }
        return false;
    }

    public static boolean isChangeLogDlgShowing() {
        return (changeLogDlg != null && changeLogDlg.isShowing());
    }

    public void resetUpdating() {
        if (mMenu != null) {
            refreshAnimation.onRefreshComplete();
        }
    }

    private void getAccidents() {
        getAccidents(false);
    }

    private void getAccidents(boolean silent) {
        if (Startup.isOnline()) {
            if (mMenu != null) {
                refreshAnimation.onRefreshBeginning();
            }
            new AccidentsRequest(this, new AccidentsRequestCallback(), silent);
        } else {
            Toast.makeText(context, R.string.inet_not_available, Toast.LENGTH_LONG).show();
        }
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if(result.has("error")){
                try {
                    Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }else {
                AccidentsGeneral.refreshPoints(context, result);
            }
            resetUpdating();
        }
    }
}
