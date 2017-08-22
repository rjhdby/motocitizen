package motocitizen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;

import motocitizen.MyApp;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.maps.MyMapManager;
import motocitizen.maps.google.MyGoogleMapManager;
import motocitizen.router.Router;
import motocitizen.rows.RowFactory;
import motocitizen.user.User;
import motocitizen.utils.BounceScrollView;
import motocitizen.utils.ChangeLog;
import motocitizen.utils.Preferences;
import motocitizen.utils.Utils;

public class MainScreenActivity extends AppCompatActivity implements MyFragmentInterface {
    private static final byte LIST = 0;
    private static final byte MAP  = 1;

    private ViewGroup   mapContainer;
    private ImageButton createAccButton;
    private ImageButton toAccListButton;
    private ImageButton toMapButton;
    private View        accListView;
    private ProgressBar progressBar;
    private ViewGroup   listContent;

    private MenuItem     refreshItem;
    private MyMapManager map;
    private boolean inTransaction = false;
    private byte    currentScreen = LIST;
    private static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);
        actionBar = getSupportActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLocationManager.getInstance().wakeup(this);

        if (Preferences.Companion.getInstance(this).getNewVersion()) {
            AlertDialog changeLogDlg = ChangeLog.INSTANCE.getDialog(this);
            changeLogDlg.show();
            Preferences.Companion.getInstance(this).setNewVersion(false);
        }
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            this.findViewById(R.id.dial_button).setEnabled(false);
        }

        if (accListView == null) accListView = this.findViewById(R.id.acc_list);
        if (progressBar == null)
            progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        if (mapContainer == null)
            mapContainer = (ViewGroup) this.findViewById(R.id.google_map);
        if (createAccButton == null)
            createAccButton = (ImageButton) this.findViewById(R.id.add_point_button);
        if (toAccListButton == null)
            toAccListButton = (ImageButton) this.findViewById(R.id.list_button);
        if (toMapButton == null)
            toMapButton = (ImageButton) this.findViewById(R.id.map_button);

        createAccButton.setOnClickListener(v -> Router.INSTANCE.goTo(this, Router.Target.CREATE));
        toAccListButton.setOnClickListener(v -> setScreen(LIST));
        toMapButton.setOnClickListener(v -> setScreen(MAP));
        this.findViewById(R.id.dial_button).setOnClickListener(v -> Router.INSTANCE.dial(this, getString(R.string.phone)));
        ((BounceScrollView) this.findViewById(R.id.accListRefresh)).setOverScrollListener(this::getAccidents);
        listContent = (ViewGroup) this.findViewById(R.id.accListContent);

        if (map == null) map = new MyGoogleMapManager(this);
        Dexter.withActivity(this)
              .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(new BasePermissionListener() {
                  @Override
                  public void onPermissionGranted(PermissionGrantedResponse response) {
                      map.enableLocation();
                  }
              }).check();

        setPermissions();
        setScreen(currentScreen);
        redraw();
        getAccidents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLocationManager.getInstance().sleep(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("toMap")) {
            //mainFragment.toMap(intent.getExtras().getInt("toMap", 0));
            toMap(intent.getExtras().getInt("toMap", 0));
            intent.removeExtra("toMap");
        }
        if (intent.hasExtra("toDetails")) {
            intent.removeExtra("toDetails");
        }
        setIntent(intent);
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

    @Override
    public void setPermissions() {
        createAccButton.setVisibility(User.getInstance(this).isStandard() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void redraw() {
        listContent.removeAllViews();

        //TODO YesterdayRow ???
        //TODO Нет событий

        for (Accident accident : Content.INSTANCE.getListReversed()) {
            if (accident.isInvisible(this)) continue;
            listContent.addView(RowFactory.INSTANCE.make(this, accident));
        }
        map.placeAccidents(this);
    }

    private void getAccidents() {
        if (inTransaction) return;
        if (MyApp.isOnline(this)) {
            startRefreshAnimation();
            Content.INSTANCE.requestUpdate(result -> {
                this.runOnUiThread(() -> {
                    stopRefreshAnimation();
                    redraw();
                });
            });
        } else {
            Toast.makeText(this, getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
    }

    private void setRefreshAnimation(boolean status) {
        progressBar.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        inTransaction = status;
        //TODO костыль
        if (refreshItem != null) refreshItem.setVisible(!status);
    }

    private void stopRefreshAnimation() {
        setRefreshAnimation(false);
    }

    private void startRefreshAnimation() {
        setRefreshAnimation(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.small_settings_menu, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
        if (inTransaction) refreshItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.small_menu_refresh:
                getAccidents();
                return true;
            case R.id.small_menu_settings:
                Router.INSTANCE.goTo(this, Router.Target.SETTINGS);
                return true;
            case R.id.small_menu_about:
                Router.INSTANCE.goTo(this, Router.Target.ABOUT);
                return true;
            case R.id.small_menu_exit:
                Router.INSTANCE.exit(this);
                return true;
            case R.id.action_refresh:
                getAccidents();
                return true;
            case R.id.do_not_disturb:
                item.setIcon(Preferences.Companion.getInstance(this).getDoNotDisturb() ? R.drawable.ic_lock_ringer_on_alpha : R.drawable.ic_lock_ringer_off_alpha);
                Preferences.Companion.getInstance(this).setDoNotDisturb(!Preferences.Companion.getInstance(this).getDoNotDisturb());
                return true;
        }
        return false;
    }

    private void setScreen(byte target) {
        currentScreen = target;
        toAccListButton.setAlpha(target == LIST ? 1f : 0.3f);
        toMapButton.setAlpha(target == MAP ? 1f : 0.3f);
        accListView.animate().translationX(target == LIST ? 0 : -Utils.getWidth(this) * 2);
        mapContainer.animate().translationX(target == MAP ? 0 : Utils.getWidth(this) * 2);
    }

    public void toMap(int id) {
        setScreen(MAP);
        map.jumpToPoint(Content.INSTANCE.getAccidents().get(id).getLocation());
    }
}
