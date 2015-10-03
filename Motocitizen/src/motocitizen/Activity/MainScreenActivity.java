package motocitizen.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import motocitizen.MyApp;
import motocitizen.gcm.NewAccidentReceived;
import motocitizen.main.R;
import motocitizen.maps.google.MyGoogleMapManager;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.utils.ChangeLog;
import motocitizen.utils.Const;
import motocitizen.utils.Preferences;

public class MainScreenActivity extends ActionBarActivity {

    private static ActionBar   actionBar;
    private static ProgressBar progressBar;
    public static  Menu        mMenu;
    public static  boolean     inTransaction;

    private   ImageButton createAccButton;
    private   RadioGroup  mainTabsGroup;
    private   View        accListView;
    private   View        mapContainer;
    protected boolean     fromDetails;

    static {
        inTransaction = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);
        MyApp.setCurrentActivity(this);

        MyApp.setMap(new MyGoogleMapManager());

        actionBar = getSupportActionBar();
        createAccButton = (ImageButton) findViewById(R.id.mc_add_point_button);
        mainTabsGroup = (RadioGroup) findViewById(R.id.main_tabs_group);
        accListView = findViewById(R.id.mc_acc_list);
        mapContainer = findViewById(R.id.map_container);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mapContainer.setTranslationX(Const.getWidth());

        createAccButton.setOnClickListener(new CreateOnClickListener());
        mainTabsGroup.setOnCheckedChangeListener(new MainTabsListener());
        findViewById(R.id.dial_button).setOnClickListener(new DialOnClickListener());

        if (ChangeLog.isNewVersion()) {
            AlertDialog changeLogDlg = ChangeLog.getDialog(true);
            changeLogDlg.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.setCurrentActivity(this);

        createAccButton.setVisibility(MyApp.getRole().isStandart() ? View.VISIBLE : View.INVISIBLE);
        MyApp.getLocationManager().wakeup();
        getAccidents();
        MyApp.getContent().redraw();
        redirectIfNeeded(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.getLocationManager().sleep();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void redirectIfNeeded(Intent intent) {
        String id    = intent.getStringExtra("id");
        int    toMap = intent.getIntExtra("toMap", 0);

        if (toMap != 0) {
            intent.removeExtra("toMap");
            mainTabsGroup.check(R.id.tab_map_button);
            fromDetails = intent.getBooleanExtra("fromDetails", false);
        } else if (id != null) {
            intent.removeExtra("id");
            MyApp.getContent().refresh();
            MyApp.toDetails(Integer.parseInt(id));
            NewAccidentReceived.clearAll();
        }
    }

    private void getAccidents() {
        if (MyApp.isOnline()) {
            startRefreshAnimation();
            MyApp.getContent().update(new AccidentsRequestCallback());
        } else {
            Toast.makeText(this, getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
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

    public static void setRefreshAnimation(boolean status) {
        progressBar.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        inTransaction = status;
        if (mMenu == null) return;
        MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
        refreshItem.setVisible(!status);
    }

    public static void stopRefreshAnimation() {
        setRefreshAnimation(false);
    }

    public static void startRefreshAnimation() {
        setRefreshAnimation(true);
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

    private class DialOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            //TODO Сделать забор телефона из преференсов
            intent.setData(Uri.parse("tel:+" + Const.PHONE));
            MyApp.getCurrentActivity().startActivity(intent);
        }
    }

    private class CreateOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MyApp.getCurrentActivity(), CreateAccActivity.class));
        }
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {

        public void onTaskComplete(JSONObject result) {
            stopRefreshAnimation();
            if (!result.has("error")) MyApp.getContent().parseJSON(result);
            MyApp.getContent().redraw();
            MyApp.getMap().placeAccidents();
        }
    }

    private class MainTabsListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            fromDetails = false;
            accListView.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.VISIBLE);
            switch (radioGroup.getCheckedRadioButtonId()) {
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
    }
}
