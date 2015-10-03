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
import android.widget.CheckBox;
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
import motocitizen.content.AccidentStatus;
import motocitizen.content.Medicine;
import motocitizen.content.Type;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.CreateAccidentRequest;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;
import motocitizen.utils.Preferences;

public class CreateAccActivity extends FragmentActivity implements View.OnClickListener {
    /* constants */
    private static final int RADIUS      = 1000;
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

    {
        confirmLock = false;
        currentScreen = MAP;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setCurrentActivity(this);
        setContentView(R.layout.create_point);
        initialLocation = MyApp.getLocationManager().getLocation();
        accident = createDefaultAccident();
        map = makeMap();
        confirmButton = (Button) findViewById(R.id.CREATE);
        EditText createFinalText = (EditText) findViewById(R.id.create_final_text);
        createFinalText.addTextChangedListener(new FinalTextWatcher());

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
            accident.put("type", Type.OTHER.toCode());
            accident.put("med", Medicine.UNKNOWN.toCode());
            accident.put("m", new JSONArray("[]"));
            accident.put("h", new JSONArray("[]"));
            accident.put("v", new JSONArray("[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Accident(accident);
    }

    private GoogleMap makeMap() {
        FragmentManager    fragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(R.id.create_map_container);
        GoogleMap          map             = mapFragment.getMap();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(accident.getLocation()), 16));
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (!MyApp.getRole().isModerator()) {
            CircleOptions circleOptions = new CircleOptions().center(MyUtils.LocationToLatLng(initialLocation)).radius(RADIUS).fillColor(0x20FF0000);
            map.addCircle(circleOptions);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition camera) {
                    Button mcCreateFineAddressConfirm = (Button) findViewById(R.id.ADDRESS);
                    mcCreateFineAddressConfirm.setEnabled(false);
                    if (initialLocation == null) return;
                    double distance = MyUtils.LatLngToLocation(camera.target).distanceTo(initialLocation);
                    mcCreateFineAddressConfirm.setEnabled(distance < RADIUS);
                }
            });
        }
        map.clear();
        for (int id : MyApp.getContent().getIds()) {
            motocitizen.accident.Accident point = MyApp.getContent().get(id);
            if (point.isInvisible()) continue;
            String title = point.getType().toString();
            title += point.getMedicine() == Medicine.NO ? "" : ", " + point.getMedicine().toString();
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
            map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())).title(title).icon(point.getType().getIcon()).alpha(alpha));
        }
        return map;
    }

    private void setComplete() {
        this.complete = accident.isAccident() || accident.getDescription().length() > 6;
        setConfirm(isComplete());
    }

    private void setUpScreen(int id) {
        hideAll();
        findViewById(id).setVisibility(View.VISIBLE);
        currentScreen = id;
        findViewById(R.id.BACK).setEnabled(id != MAP);
    }

    private void refreshDescription() {
        String text = accident.getType().toString();
        text += accident.getMedicine() == Medicine.UNKNOWN ? "" : ". " + accident.getMedicine().toString();
        ((TextView) findViewById(R.id.create_what)).setText(text);
        ((TextView) findViewById(R.id.create_who)).setText(MyApp.getAuth().getLogin());
        ((TextView) findViewById(R.id.create_where)).setText(accident.getAddress());
        ((TextView) findViewById(R.id.create_when)).setText(Const.DATE_FORMAT.format(accident.getTime()));
    }

    private void setupListener() {
        Integer[] ids = {R.id.BREAK, R.id.STEAL, R.id.OTHER, R.id.ACCIDENT, R.id.MOTO_AUTO, R.id.SOLO, R.id.MOTO_MOTO, R.id.MOTO_MAN, R.id.PEOPLE_OK, R.id.PEOPLE_LIGHT, R.id.PEOPLE_HEAVY, R.id.PEOPLE_LETHAL, R.id.PEOPLE_UNKNOWN, R.id.ADDRESS, R.id.CREATE, R.id.CANCEL, R.id.BACK};
        for (int id : ids) findViewById(id).setOnClickListener(this);
    }

    private void setConfirm(Boolean status) {
        if (!confirmLock) confirmButton.setEnabled(status);
    }

    private boolean isComplete() {
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
                accident.setAddress(MyApp.getLocationManager().getAddress(accident.getLocation()));
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
        CreateAccidentRequest request = new CreateAccidentRequest(new CreateAccidentCallback());
        request.setType(accident.getType());
        request.setMed(accident.getMedicine());
        request.setAddress(accident.getAddress());
        request.setLocation(accident.getLocation());
        request.setDescription(accident.getDescription());
        request.setCreated(accident.getTime());
        if (((CheckBox) findViewById(R.id.forStat)).isChecked()) request.setForStat();
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

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.setCurrentActivity(this);
    }

    private class FinalTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            accident.setDescription(s.toString());
            setComplete();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
