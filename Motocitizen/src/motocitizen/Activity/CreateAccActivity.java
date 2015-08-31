package motocitizen.Activity;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.app.general.user.Role;
import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.content.AccidentStatus;
import motocitizen.content.Type;
import motocitizen.draw.Resources;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.network.requests.CreateAccidentRequest;
import motocitizen.startup.Preferences;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;

public class CreateAccActivity extends FragmentActivity implements View.OnClickListener {
    private final int RADIUS      = 1000;
    private final int TYPE        = R.id.mc_create_type_frame;
    private final int DESCRIPTION = R.id.mc_create_final_frame;
    private final int ACCIDENT    = R.id.mc_create_acc_frame;
    private final int MEDICINE    = R.id.mc_create_people_frame;
    private final int MAP         = R.id.mc_create_map;
    private Accident  accident;
    private Boolean   confirmLock;
    private int       currentScreen;
    private GoogleMap map;
    private Button    confirmButton;
    private boolean   complete;
    private Location  initialLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_point);
        initialLocation = MyLocationManager.getLocation(this);
        accident = createDefaultAccident();
        confirmLock = false;
        map = makeMap();
        currentScreen = MAP;

        confirmButton = (Button) findViewById(R.id.CREATE);
        ((EditText) findViewById(R.id.mc_create_final_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                accident.setDescription(s.toString());
                setComplete();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
            accident.put("owner_id", Preferences.getUserId());
            accident.put("owner", Preferences.getUserName());
            accident.put("status", AccidentStatus.ACTIVE.toCode());
            accident.put("uxtime", String.valueOf(System.currentTimeMillis() / 1000L));
            accident.put("address", "");
            accident.put("descr", "");
            accident.put("mc_accident_orig_type", Type.OTHER.toCode());
            accident.put("mc_accident_orig_med", Medicine.UNKNOWN.toCode());
            accident.put("messages", new JSONArray("[]"));
            accident.put("history", new JSONArray("[]"));
            accident.put("onway", new JSONArray("[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Accident(this, accident);
    }

    private GoogleMap makeMap() {
        FragmentManager    fragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mc_create_map_container);
        GoogleMap          map             = mapFragment.getMap();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(accident.getLocation()), 16));
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (!Role.isModerator()) {
            CircleOptions circleOptions = new CircleOptions().center(MyUtils.LocationToLatLng(initialLocation)).radius(RADIUS).fillColor(0x20FF0000);
            map.addCircle(circleOptions);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition camera) {
                    Button mcCreateFineAddressConfirm = (Button) findViewById(R.id.ADDRESS);
                    if (initialLocation != null) {
                        double distance = MyUtils.LatLngToLocation(camera.target).distanceTo(initialLocation);
                        if (distance > RADIUS) {
                            mcCreateFineAddressConfirm.setEnabled(false);
                        } else {
                            mcCreateFineAddressConfirm.setEnabled(true);
                        }
                    } else {
                        mcCreateFineAddressConfirm.setEnabled(false);
                    }
                }
            });
        }
        map.clear();
        for (int id : Content.getIds()) {
            motocitizen.accident.Accident point = Content.get(id);
            if (point.isInvisible()) continue;
            String title = point.getType().toString();
            if (point.getMedicine() != Medicine.NO) {
                title += ", " + point.getMedicine().toString();
            }
            title += ", " + MyUtils.getIntervalFromNowInText(point.getTime()) + " назад";

            float alpha;
            int age = (int) (((new Date()).getTime() - point.getTime().getTime()) / 3600000);
            if (age < 2) {
                alpha = 1.0f;
            } else if (age < 6) {
                alpha = 0.5f;
            } else {
                alpha = 0.2f;
            }
            map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())).title(title).icon(Resources.getMapBitmapDescriptor(point.getType())).alpha(alpha));
        }
        return map;
    }

    public void setComplete() {
        this.complete = accident.isAccident() || accident.getDescription().length() > 6;
        setConfirm(isComplete());
    }

    private void setUpScreen(int id) {
        hideAll();
        findViewById(id).setVisibility(View.VISIBLE);
        currentScreen = id;
        if (id == MAP) {
            findViewById(R.id.BACK).setEnabled(false);
        } else {
            findViewById(R.id.BACK).setEnabled(true);
        }
    }

    private void refreshDescription() {
        if (accident.getMedicine() == Medicine.UNKNOWN) {
            ((TextView) findViewById(R.id.mc_create_what)).setText(accident.getType().toString());
        } else {
            ((TextView) findViewById(R.id.mc_create_what)).setText(accident.getType().toString() + ". " + accident.getMedicine().toString());
        }
        ((TextView) findViewById(R.id.mc_create_who)).setText(Content.auth.getLogin());
        ((TextView) findViewById(R.id.mc_create_where)).setText(accident.getAddress());
        ((TextView) findViewById(R.id.mc_create_when)).setText(Const.DATE_FORMAT.format(accident.getTime()));
    }

    private void setupListener() {
        Integer[] ids = {R.id.BREAK, R.id.STEAL, R.id.OTHER, R.id.ACCIDENT, R.id.MOTO_AUTO, R.id.SOLO, R.id.MOTO_MOTO, R.id.MOTO_MAN, R.id.PEOPLE_OK, R.id.PEOPLE_LIGHT, R.id.PEOPLE_HEAVY, R.id.PEOPLE_LETHAL, R.id.PEOPLE_UNKNOWN, R.id.ADDRESS, R.id.CREATE, R.id.CANCEL, R.id.BACK};
        for (int id : ids) findViewById(id).setOnClickListener(this);
    }

    private void setConfirm(Boolean status) {
        if (!confirmLock) confirmButton.setEnabled(status);
    }

    public boolean isComplete() {
        return complete;
    }

    private void hideAll() {
        Integer[] ids = {TYPE, MAP, MEDICINE, DESCRIPTION, ACCIDENT};
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
                accident.setAddress((new MyApp()).getAddres(accident.getLocation()));
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
        CreateAccidentRequest request = new CreateAccidentRequest(new CreateAccidentCallback(), this);
        request.setType(accident.getType());
        request.setMed(accident.getMedicine());
        request.setAddress(accident.getAddress());
        request.setLocation(accident.getLocation());
        request.setDescription(accident.getDescription());
        request.setCreated(accident.getTime());
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
                if (accident.isAccident()) {
                    setUpScreen(MEDICINE);
                } else {
                    setUpScreen(TYPE);
                }
                break;
        }
        setInComplete();
        refreshDescription();
    }

    private void disableConfirm() {
        setConfirm(false);
        confirmLock = true;
    }

    public void setInComplete() {
        this.complete = false;
        setConfirm(false);
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK:
                backButton();
                return true;
        }
        return super.onKeyUp(keycode, e);
    }

    private void enableConfirm() {
        confirmLock = false;
        setConfirm(true);
    }

    private void message(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private class CreateAccidentCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    message(result.getString("error"));
                } catch (JSONException e) {
                    message("Неизвестная ошибка" + result.toString());
                    e.printStackTrace();
                }
            } else {
                finish();
            }
            enableConfirm();
        }
    }
}
