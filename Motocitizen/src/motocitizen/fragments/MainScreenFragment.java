package motocitizen.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import motocitizen.content.accident.Accident;
import motocitizen.activity.MyFragmentInterface;
import motocitizen.dictionary.Content;
import motocitizen.main.R;
import motocitizen.maps.MyMapManager;
import motocitizen.maps.google.MyGoogleMapManager;
import motocitizen.router.Router;
import motocitizen.user.User;
import motocitizen.utils.BounceScrollView;
import motocitizen.utils.Const;
import motocitizen.utils.Preferences;

public class MainScreenFragment extends Fragment implements MyFragmentInterface {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.main_screen_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (accListView == null) accListView = getActivity().findViewById(R.id.acc_list);
        if (progressBar == null) progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        if (mapContainer == null) mapContainer = (ViewGroup) getActivity().findViewById(R.id.google_map);
        if (createAccButton == null) createAccButton = (ImageButton) getActivity().findViewById(R.id.add_point_button);
        if (toAccListButton == null) toAccListButton = (ImageButton) getActivity().findViewById(R.id.list_button);
        if (toMapButton == null) toMapButton = (ImageButton) getActivity().findViewById(R.id.map_button);

        createAccButton.setOnClickListener(v -> Router.goTo(getActivity(), Router.Target.CREATE));
        toAccListButton.setOnClickListener(v -> setScreen(LIST));
        toMapButton.setOnClickListener(v -> setScreen(MAP));
        getActivity().findViewById(R.id.dial_button).setOnClickListener(v -> Router.dial(getActivity(), Const.PHONE));
        ((BounceScrollView) getActivity().findViewById(R.id.accListRefresh)).setOverScrollListener(this::getAccidents);
        listContent = (ViewGroup) getActivity().findViewById(R.id.accListContent);


        if (map == null) map = new MyGoogleMapManager(getActivity());

        Dexter.withActivity(getActivity())
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
    public void setPermissions() {
        createAccButton.setVisibility(User.getInstance(getActivity()).isStandard() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void redraw() {
        listContent.removeAllViews();

        //TODO YesterdayRow ???
        //TODO Нет событий

        Content points = Content.getInstance();
        for (int id : points.reverseSortedKeySet()) {
            Accident accident = points.get(id);
            if (accident.isInvisible(getActivity())) continue;
            listContent.addView(accident.makeListRow(getContext()));
        }
        map.placeAccidents(getActivity());
    }

    private void getAccidents() {
        if (inTransaction) return;
        if (MyApp.isOnline(getContext())) {
            startRefreshAnimation();
            Content.getInstance().requestUpdate(result -> {
                if (!result.has("error")) Content.getInstance().parseJSON(result);
                if (!isVisible()) return;
                stopRefreshAnimation();
                redraw();
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.small_settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        refreshItem = menu.findItem(R.id.action_refresh);
        if (inTransaction) refreshItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.small_menu_refresh:
                getAccidents();
                return true;
            case R.id.small_menu_settings:
                Router.goTo(getActivity(), Router.Target.SETTINGS);
                return true;
            case R.id.small_menu_about:
                Router.goTo(getActivity(), Router.Target.ABOUT);
                return true;
            case R.id.small_menu_exit:
                Router.exit(getActivity());
                return true;
            case R.id.action_refresh:
                getAccidents();
                return true;
            case R.id.do_not_disturb:
                item.setIcon(Preferences.Companion.getInstance(getActivity()).getDoNotDisturb() ? R.drawable.ic_lock_ringer_on_alpha : R.drawable.ic_lock_ringer_off_alpha);
                Preferences.Companion.getInstance(getActivity()).setDoNotDisturb(!Preferences.Companion.getInstance(getActivity()).getDoNotDisturb());
                return true;
        }
        return false;
    }

    private void setScreen(byte target) {
        currentScreen = target;
        toAccListButton.setAlpha(target == LIST ? 1f : 0.3f);
        toMapButton.setAlpha(target == MAP ? 1f : 0.3f);
        accListView.animate().translationX(target == LIST ? 0 : -Const.getWidth(getActivity()) * 2);
        mapContainer.animate().translationX(target == MAP ? 0 : Const.getWidth(getActivity()) * 2);
    }

    public void toMap(int id) {
        setScreen(MAP);
        map.jumpToPoint(Content.getInstance().get(id).getLocation());
    }
}
