package motocitizen.activity;

import android.Manifest;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import motocitizen.content.accident.Accident;
import motocitizen.dictionary.AccidentStatus;
import motocitizen.dictionary.Content;
import motocitizen.dictionary.Medicine;
import motocitizen.dictionary.Type;
import motocitizen.geocoder.MyGeoCoder;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.network.GeocoderClient;
import motocitizen.network.requests.CreateAccidentRequest;
import motocitizen.user.User;
import motocitizen.utils.DateUtils;
import motocitizen.utils.MyUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.ToastUtils;

public class CreateAccActivity extends FragmentActivity implements View.OnClickListener {
    /* constants */
    private static final int RADIUS      = 2000;
    private static final int TYPE        = R.id.create_type_frame;
    private static final int DESCRIPTION = R.id.create_final_frame;
    private static final int ACCIDENT    = R.id.create_acc_frame;
    private static final int MEDICINE    = R.id.create_people_frame;
    private static final int MAP         = R.id.create_map;
    /* end of constants */

    private int       currentScreen;
    private boolean   confirmLock;
    private boolean   complete;
    private Accident  accident;
    private GoogleMap map;
    private Button    confirmButton;
    private Location  initialLocation;
    private EditText  searchEditText;

    {
        confirmLock = false;
        currentScreen = MAP;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_point);
        initialLocation = MyLocationManager.getInstance().getLocation();
        accident = createDefaultAccident();
        makeMap();
        confirmButton = (Button) findViewById(R.id.CREATE);
        EditText createFinalText = (EditText) findViewById(R.id.create_final_text);
        createFinalText.addTextChangedListener(new FinalTextWatcher());
        searchEditText = (EditText) findViewById(R.id.SearchEditText);

        setUpScreen(MAP);
        refreshDescription();
        setupListener();
    }

    private Accident createDefaultAccident() {
        JSONObject accident = new JSONObject();
        try {
            accident.put("id", 0);
            accident.put("lat", initialLocation.getLatitude());
            accident.put("lon", initialLocation.getLongitude());
            accident.put("owner_id", Preferences.getInstance().getUserId());
            accident.put("owner", Preferences.getInstance().getUserName());
            accident.put("status", AccidentStatus.ACTIVE.code());
            accident.put("uxtime", String.valueOf(System.currentTimeMillis() / 1000L));
            accident.put("address", "");
            accident.put("descr", "");
            accident.put("type", Type.OTHER.code());
            accident.put("med", Medicine.UNKNOWN.code());
            accident.put("m", new JSONArray("[]"));
            accident.put("h", new JSONArray("[]"));
            accident.put("v", new JSONArray("[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Accident(accident);
    }

    @SuppressWarnings({ "MissingPermission" })
    private void enableMyLocation() {
        Dexter.withActivity(this)
              .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(new BasePermissionListener() {
                  @Override
                  public void onPermissionGranted(PermissionGrantedResponse response) {
                      map.setMyLocationEnabled(true);
                  }
              }).check();
    }

    private class OnMapCreated implements OnMapReadyCallback {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(accident.getLocation()), 16));
            enableMyLocation();
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            if (!User.getInstance().isModerator()) {
                //Прячем кнопки поиска адреса
                searchEditText.setVisibility(View.GONE);
                ImageButton searchButton = (ImageButton) findViewById(R.id.SEARCH);
                searchButton.setVisibility(View.GONE);

                CircleOptions circleOptions = new CircleOptions().center(MyUtils.LocationToLatLng(initialLocation)).radius(RADIUS).fillColor(0x20FF0000);
                map.addCircle(circleOptions);
                map.setOnCameraMoveCanceledListener(() -> {
                    Button mcCreateFineAddressConfirm = (Button) findViewById(R.id.ADDRESS);
                    mcCreateFineAddressConfirm.setEnabled(false);
                    if (initialLocation == null) return;
                    double distance = MyUtils.LatLngToLocation(map.getCameraPosition().target).distanceTo(initialLocation);
                    mcCreateFineAddressConfirm.setEnabled(distance < RADIUS);
                });
            }
            map.clear();
            for (int id : Content.getInstance().keySet()) {
                Accident point = Content.getInstance().get(id);
                if (point.isInvisible()) continue;
                String title = point.getType().string();
                title += point.getMedicine() == Medicine.NO ? "" : ", " + point.getMedicine().string();
                title += ", " + MyUtils.getIntervalFromNowInText(CreateAccActivity.this, point.getTime()) + " назад";

                float alpha;
                int   age = (int) (((new Date()).getTime() - point.getTime().getTime()) / 3600000);
                if (age < 2) {
                    alpha = 1.0f;
                } else if (age < 6) {
                    alpha = 0.5f;
                } else {
                    alpha = 0.2f;
                }
                map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())).title(title).icon(point.getType().getIcon()).alpha(alpha));
            }
        }
    }

    private void makeMap() {
        FragmentManager    fragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(R.id.create_map_container);
        mapFragment.getMapAsync(new OnMapCreated());

    }

    private void setComplete() {
        complete = accident.isAccident() || accident.getDescription().length() > 6;
        setConfirm(isComplete());
    }

    private void setUpScreen(int id) {
        hideAll();
        findViewById(id).setVisibility(View.VISIBLE);
        currentScreen = id;
        findViewById(R.id.BACK).setEnabled(id != MAP);
    }

    private void refreshDescription() {
        String text = accident.getType().string();
        text += accident.getMedicine() == Medicine.UNKNOWN ? "" : ". " + accident.getMedicine().string();
        ((TextView) findViewById(R.id.create_what)).setText(text);
        ((TextView) findViewById(R.id.create_who)).setText(Preferences.getInstance().getLogin());
        ((TextView) findViewById(R.id.create_where)).setText(accident.getAddress());
        ((TextView) findViewById(R.id.create_when)).setText(DateUtils.getDateTime(accident.getTime()));
    }

    private void setupListener() {
        Integer[] ids = { R.id.BREAK,
                          R.id.STEAL,
                          R.id.OTHER,
                          R.id.ACCIDENT,
                          R.id.MOTO_AUTO,
                          R.id.SOLO,
                          R.id.MOTO_MOTO,
                          R.id.MOTO_MAN,
                          R.id.PEOPLE_OK,
                          R.id.PEOPLE_LIGHT,
                          R.id.PEOPLE_HEAVY,
                          R.id.PEOPLE_LETHAL,
                          R.id.PEOPLE_UNKNOWN,
                          R.id.ADDRESS,
                          R.id.CREATE,
                          R.id.CANCEL,
                          R.id.BACK,
                          R.id.SEARCH };
        for (int id : ids) findViewById(id).setOnClickListener(this);
    }

    private void setConfirm(Boolean status) {
        if (!confirmLock) confirmButton.setEnabled(status);
    }

    private boolean isComplete() {
        return complete;
    }

    private void hideAll() {
        Integer[] ids = { TYPE, MAP, MEDICINE, DESCRIPTION, ACCIDENT };
        for (int id : ids) findViewById(id).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.BREAK:
            case R.id.STEAL:
            case R.id.OTHER:
                accident.setType(getSelectedType(id));
                setUpScreen(DESCRIPTION);
                break;
            case R.id.MOTO_AUTO:
            case R.id.SOLO:
            case R.id.MOTO_MOTO:
            case R.id.MOTO_MAN:
                accident.setType(getSelectedType(id));
                setUpScreen(MEDICINE);
                break;
            case R.id.ACCIDENT:
                setUpScreen(ACCIDENT);
                break;
            case R.id.PEOPLE_OK:
            case R.id.PEOPLE_LIGHT:
            case R.id.PEOPLE_HEAVY:
            case R.id.PEOPLE_LETHAL:
            case R.id.PEOPLE_UNKNOWN:
                accident.setMedicine(getSelectedMedicine(id));
                setUpScreen(DESCRIPTION);
                setComplete();
                break;
            case R.id.ADDRESS:
                accident.setLatLng(map.getCameraPosition().target);
                accident.setAddress(MyLocationManager.getInstance().getAddress(accident.getLocation()));
                setUpScreen(TYPE);
                break;
            case R.id.CREATE:
                confirm();
                break;
            case R.id.CANCEL:
                //TODO Добавить подтверждение
                finish();
                break;
            case R.id.BACK:
                backButton();
                break;
            case R.id.SEARCH:
                String addressForSearch = searchEditText.getText().toString();
                try {
                    List<Address> addresses = MyGeoCoder.getInstance().getFromLocationName(addressForSearch, 1);
                    LatLng        latLng;
                    if (addresses.size() > 0) {
                        latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    } else {
                        latLng = GeocoderClient.latLngByAddress(addressForSearch);
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show(CreateAccActivity.this, CreateAccActivity.this.getString(R.string.nothing_is_found));
                }
                break;
        }
        refreshDescription();
    }

    private Type getSelectedType(int id) {
        switch (id) {
            case R.id.BREAK:
                return Type.BREAK;
            case R.id.STEAL:
                return Type.STEAL;
            case R.id.MOTO_AUTO:
                return Type.MOTO_AUTO;
            case R.id.SOLO:
                return Type.SOLO;
            case R.id.MOTO_MOTO:
                return Type.MOTO_MOTO;
            case R.id.MOTO_MAN:
                return Type.MOTO_MAN;
            case R.id.OTHER:
            default:
                return Type.OTHER;
        }
    }

    private Medicine getSelectedMedicine(int id) {
        switch (id) {
            case R.id.PEOPLE_OK:
                return Medicine.NO;
            case R.id.PEOPLE_LIGHT:
                return Medicine.LIGHT;
            case R.id.PEOPLE_HEAVY:
                return Medicine.HEAVY;
            case R.id.PEOPLE_LETHAL:
                return Medicine.LETHAL;
            case R.id.PEOPLE_UNKNOWN:
            default:
                return Medicine.UNKNOWN;
        }
    }

    private void confirm() {
        disableConfirm();
        CreateAccidentRequest request = new CreateAccidentRequest(result -> {
            if (!result.has("error")) {
                finish();
            } else {
                ToastUtils.show(CreateAccActivity.this, result.optString("error", "Неизвестная ошибка"));
                enableConfirm();
            }
        });
        request.setType(accident.getType());
        request.setMed(accident.getMedicine());
        request.setAddress(accident.getAddress());
        request.setLocation(accident.getLocation());
        request.setDescription(accident.getDescription());
        request.setCreated(accident.getTime());
        if (((CheckBox) findViewById(R.id.forStat)).isChecked()) request.setForStat();
        //noinspection unchecked
        request.execute();
    }

    private void backButton() {
        switch (currentScreen) {
            case MAP:
                finish();
                break;
            case MEDICINE:
                setUpScreen(ACCIDENT);
                break;
            case ACCIDENT:
                setUpScreen(TYPE);
                break;
            case TYPE:
                setUpScreen(MAP);
                break;
            case DESCRIPTION:
                setUpScreen(accident.isAccident() ? MEDICINE : TYPE);
                break;
        }
        setInComplete();
        refreshDescription();
    }

    private void disableConfirm() {
        setConfirm(false);
        confirmLock = true;
    }

    private void setInComplete() {
        this.complete = false;
        setConfirm(false);
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK:
                backButton();
                return true;
            default: return super.onKeyUp(keycode, e);
        }
    }

    private void enableConfirm() {
        confirmLock = false;
        setConfirm(true);
    }

    private class FinalTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            accident.setDescription(s.toString());
            setComplete();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
