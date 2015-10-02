package motocitizen.startup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.Activity.AboutActivity;
import motocitizen.Activity.CreateAccActivity;
import motocitizen.Activity.SettingsActivity;
import motocitizen.MyApp;
import motocitizen.content.Content;
import motocitizen.gcm.GCMBroadcastReceiver;
import motocitizen.gcm.GCMRegistration;
import motocitizen.gcm.NewAccidentReceived;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.maps.MyMapManager;
import motocitizen.maps.google.MyGoogleMapManager;
import motocitizen.maps.osm.MyOSMMapManager;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.utils.Const;

public class Startup extends ActionBarActivity implements View.OnClickListener {

    public static  MyMapManager                       map;
    public static  boolean                            fromDetails;
    public static  Menu                               mMenu;
    public static  Integer                            currentGeneral;
    private static ActionBar                          actionBar;
    private static AlertDialog                        changeLogDlg;
    private        ImageButton                        createAccButton;
    private        RadioGroup                         mainTabsGroup;
    private        View                               accListView;
    private        View                               mapContainer;
    static         ProgressBar                        progressBar;
    public static  boolean                            inTransaction;
    private final  RadioGroup.OnCheckedChangeListener mainTabsListener;
    public static  long                               start;

    static {
        changeLogDlg = null;
        inTransaction = false;
        start = (new Date()).getTime();
    }

    {
        mainTabsListener = new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentGeneral = group.getCheckedRadioButtonId();
                fromDetails = false;
                accListView.setVisibility(View.VISIBLE);
                mapContainer.setVisibility(View.VISIBLE);
                switch (currentGeneral) {
                    case R.id.tab_map_button:
                        accListView.animate().translationX(-Const.getWidth() * 2);
                        mapContainer.animate().translationX(0);
                        break;
                    case R.id.tab_accidents_button:
                    default:
                        accListView.animate().translationX(0);
                        mapContainer.animate().translationX(Const.getWidth() * 2);
                        break;
                }
            }
        };
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

    public static boolean isChangeLogDlgShowing() {
        return (changeLogDlg != null && changeLogDlg.isShowing());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setCurrentActivity(this);
        Log.d("START ON_CREATE", String.valueOf((new Date()).getTime() - start));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.main);
        actionBar = getSupportActionBar();
        Preferences.setDoNotDisturb(false);
        Log.d("START CHECK UPDATE", String.valueOf((new Date()).getTime() - start));
        checkUpdate();
        ImageButton dialButton = (ImageButton) findViewById(R.id.dial_button);
        dialButton.setOnClickListener(this);

        createAccButton = (ImageButton) findViewById(R.id.mc_add_point_button);
        createAccButton.setOnClickListener(this);

        mainTabsGroup = (RadioGroup) findViewById(R.id.main_tabs_group);
        mainTabsGroup.setOnCheckedChangeListener(mainTabsListener);

        accListView = findViewById(R.id.mc_acc_list);
        mapContainer = findViewById(R.id.map_container);
        mapContainer.setTranslationX(Const.getWidth());

        accListView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return true;
            }
        });
        Log.d("START LOCATION MANAGER", String.valueOf((new Date()).getTime() - start));
        new MyLocationManager();
        Log.d("START GCM REGISTRATION", String.valueOf((new Date()).getTime() - start));
        new GCMRegistration();
        Log.d("START CONTENT", String.valueOf((new Date()).getTime() - start));
        new Content();
        Log.d("START CREATE MAP", String.valueOf((new Date()).getTime() - start));
        createMap(MyMapManager.GOOGLE);
        Log.d("START BROADCAST", String.valueOf((new Date()).getTime() - start));
        new GCMBroadcastReceiver();
        Log.d("START END ON_CREATE", String.valueOf((new Date()).getTime() - start));
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
        if (!Preferences.getCurrentVersion().equals(version)) {
            changeLogDlg = ChangeLog.getDialog(this, true);
            changeLogDlg.show();
        }
        Preferences.setCurrentVersion(version);
    }

    public static void createMap(String name) {
        if (map != null && map.getName().equals(name)) return;
        switch (name) {
            case MyMapManager.OSM:
                map = new MyOSMMapManager(MyApp.getCurrentActivity());
                break;
            case MyMapManager.GOOGLE:
            default:
                map = new MyGoogleMapManager();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLocationManager.sleep();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.setCurrentActivity(this);
        Log.d("START ON_RESUME", String.valueOf((new Date()).getTime() - start));
        MyLocationManager.wakeup();
        Intent intent = getIntent();
        String id     = intent.getStringExtra("id");
        int    toMap  = intent.getIntExtra("toMap", 0);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        createAccButton.setVisibility(Content.auth.getRole().isStandart() ? View.VISIBLE : View.INVISIBLE);
        Log.d("START REDRAW", String.valueOf((new Date()).getTime() - start));
        Content.redraw();
        Log.d("START GET ACCIDENTS", String.valueOf((new Date()).getTime() - start));
        getAccidents();

        if (toMap != 0) {
            intent.removeExtra("toMap");
            mainTabsGroup.check(R.id.tab_map_button);
            fromDetails = intent.getBooleanExtra("fromDetails", false);
        } else if (id != null) {
            intent.removeExtra("id");
            Content.refresh();
            Content.toDetails(Integer.parseInt(id));
            NewAccidentReceived.clearAll();
        }
        Log.d("START END ON_RESUME", String.valueOf((new Date()).getTime() - start));
    }

    private void getAccidents() {
        if (Startup.isOnline()) {
            startRefreshAnimation();
            Content.update(new AccidentsRequestCallback(this));
        } else {
            message(getString(R.string.inet_not_available));
        }
    }

    public static boolean isOnline() {
        ConnectivityManager cm      = (ConnectivityManager) MyApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void message(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK:
                /*
                if (fromDetails) {
                    AccidentsGeneral.toDetails(this);
                }
                */
                Content.redraw();
                return true;
        }
        return super.onKeyUp(keycode, e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.small_settings_menu, menu);
        mMenu = menu;
        MenuItem itemMenuNotDisturb = mMenu.findItem(R.id.do_not_disturb);
        MenuItem refreshItem        = mMenu.findItem(R.id.action_refresh);
        if (inTransaction) refreshItem.setVisible(false);
        itemMenuNotDisturb.setIcon(Preferences.getDoNotDisturb() ? R.drawable.ic_lock_ringer_off_alpha : R.drawable.ic_lock_ringer_on_alpha);
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
                this.startActivity(intentAbout);
                return true;
            case R.id.small_menu_exit:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                return true;
            case R.id.action_refresh:
                getAccidents();
                return true;
            case R.id.do_not_disturb:
                item.setIcon(Preferences.getDoNotDisturb() ? R.drawable.ic_lock_ringer_on_alpha : R.drawable.ic_lock_ringer_off_alpha);
                Preferences.setDoNotDisturb(!Preferences.getDoNotDisturb());
                return true;
        }
        return false;
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

    private static class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        private Context context;

        public AccidentsRequestCallback(final Context context) {
            this.context = context;
        }

        public void onTaskComplete(JSONObject result) {
            stopRefreshAnimation();
            if (!result.has("error")) Content.parseJSON(result);
            Content.redraw();
            Startup.map.placeAccidents();
        }
    }

    public static void startRefreshAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        inTransaction = true;
        if (mMenu == null) return;
        MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
        refreshItem.setVisible(false);
    }

    public static void stopRefreshAnimation() {
        progressBar.setVisibility(View.INVISIBLE);
        inTransaction = false;
        if (mMenu == null) return;
        MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
        refreshItem.setVisible(true);
    }
}
