package motocitizen.Activity;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.MyApp;
import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentTypes;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.MyLocationManager;
import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.network.requests.CreateAccidentRequest;
import motocitizen.startup.MyPreferences;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;
import motocitizen.utils.Text;

public class CreateAccActivity extends FragmentActivity implements View.OnClickListener {
    private Context       context;
    private MyPreferences prefs;
    private NewAccident   accident;
    private View          listView;
    private Boolean       confirmLock;
    private final int         RADIUS              = 1000;
    private final TextWatcher DetailsTextListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            accident.setDescription(s.toString());
            setConfirm();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private int       TYPE;
    private int       DESCR;
    private int       ACC;
    private int       MED;
    private int       MAP;
    private int       currentScreen;
    private GoogleMap map;
    private Button    confirmButton;
    private Button    backButton;
    private View      typeFrame;
    private View      accFrame;
    private View      mapFrame;
    private View      medFrame;
    private View      descrFrame;
    private EditText  descrView;

    private void refreshDescription() {
        if (accident.med.equals("mc_m_na")) {
            Text.set(context, listView, R.id.mc_create_what, prefs.getAccidentTypeName(accident.type));
        } else {
            Text.set(context, listView, R.id.mc_create_what, prefs.getAccidentTypeName(accident.type) + ". " + prefs.getMedTypeName(accident.med));
        }
        Text.set(context, listView, R.id.mc_create_who, AccidentsGeneral.auth.getLogin());
        Text.set(context, listView, R.id.mc_create_where, accident.address);
        Text.set(context, listView, R.id.mc_create_when, Const.dateFormat.format(accident.created));
    }

    private void updateAddress(String address) {
        accident.address = address;
        refreshDescription();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_point);
        context = this;
        prefs = ((MyApp) context.getApplicationContext()).getPreferences();
        accident = new NewAccident();
        confirmLock = false;
        map = makeMap();
        TYPE = R.id.mc_create_type_frame;
        DESCR = R.id.mc_create_final_frame;
        ACC = R.id.mc_create_acc_frame;
        MED = R.id.mc_create_people_frame;
        MAP = R.id.mc_create_map;
        currentScreen = MAP;

        confirmButton = (Button) findViewById(R.id.mc_create_create);
        backButton = (Button) findViewById(R.id.mc_create_back);

        typeFrame = findViewById(TYPE);
        mapFrame = findViewById(MAP);
        accFrame = findViewById(ACC);
        descrFrame = findViewById(DESCR);
        medFrame = findViewById(MED);

        descrView = (EditText) findViewById(R.id.mc_create_final_text);
        descrView.addTextChangedListener(DetailsTextListener);

        listView = findViewById(R.id.mc_create_main);

        goToMap();
        refreshDescription();
        setupListener();
    }

    private void setupListener() {
        findViewById(R.id.mc_create_type_break_button).setOnClickListener(this);
        findViewById(R.id.mc_create_type_steal_button).setOnClickListener(this);
        findViewById(R.id.mc_create_type_other_button).setOnClickListener(this);
        findViewById(R.id.mc_create_type_acc_button).setOnClickListener(this);
        findViewById(R.id.mc_create_acc_ma_button).setOnClickListener(this);
        findViewById(R.id.mc_create_acc_solo_button).setOnClickListener(this);
        findViewById(R.id.mc_create_acc_mm_button).setOnClickListener(this);
        findViewById(R.id.mc_create_acc_mp_button).setOnClickListener(this);
        findViewById(R.id.mc_create_people_ok_button).setOnClickListener(this);
        findViewById(R.id.mc_create_people_light_button).setOnClickListener(this);
        findViewById(R.id.mc_create_people_hard_button).setOnClickListener(this);
        findViewById(R.id.mc_create_people_death_button).setOnClickListener(this);
        findViewById(R.id.mc_create_people_na_button).setOnClickListener(this);
        findViewById(R.id.mc_create_fine_address_confirm).setOnClickListener(this);
        findViewById(R.id.mc_create_create).setOnClickListener(this);
        findViewById(R.id.mc_create_cancel).setOnClickListener(this);
        findViewById(R.id.mc_create_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.mc_create_type_break_button:
                accident.makeBreak();
                goToDescription();
                break;
            case R.id.mc_create_type_steal_button:
                accident.makeSteal();
                goToDescription();
                break;
            case R.id.mc_create_type_other_button:
                accident.makeOther();
                goToDescription();
                break;
            // Кнопки ДТП
            case R.id.mc_create_type_acc_button:
                goToAccidentSubType();
                break;
            case R.id.mc_create_acc_ma_button:
                accident.makeAccident("acc_m_a");
                goToMed();
                break;
            case R.id.mc_create_acc_solo_button:
                accident.makeAccident("acc_m");
                goToMed();
                break;
            case R.id.mc_create_acc_mm_button:
                accident.makeAccident("acc_m_m");
                goToMed();
                break;
            case R.id.mc_create_acc_mp_button:
                accident.makeAccident("acc_m_p");
                goToMed();
                break;
            // Кнопки медицины
            case R.id.mc_create_people_ok_button:
                accident.setMed("mc_m_wo");
                goToDescription();
                break;
            case R.id.mc_create_people_light_button:
                accident.setMed("mc_m_l");
                goToDescription();
                break;
            case R.id.mc_create_people_hard_button:
                accident.setMed("mc_m_h");
                goToDescription();
                break;
            case R.id.mc_create_people_death_button:
                accident.setMed("mc_m_d");
                goToDescription();
                break;
            case R.id.mc_create_people_na_button:
                accident.setMed("mc_m_na");
                goToDescription();
                break;
            // Работа с картой
            case R.id.mc_create_fine_address_confirm:
                accident.updateLocation(MyUtils.LatLngToLocation(map.getCameraPosition().target));
                goToType();
                break;
            // Кнопки действий
            case R.id.mc_create_create:
                confirm();
                break;
            case R.id.mc_create_cancel:
                //TODO Добавить подтверждение
                finish();
                break;
            case R.id.mc_create_back:
                backButton();
                break;
        }
        setConfirm();
        refreshDescription();
    }

    private void backButton() {
        switch (currentScreen) {
            case R.id.mc_create_map: //MAP
                finish();
                break;
            case R.id.mc_create_people_frame: //MED
                goToAccidentSubType();
                accident.resetType();
                break;
            case R.id.mc_create_acc_frame: //ACC
                goToType();
                break;
            case R.id.mc_create_type_frame: //TYPE
                goToMap();
                break;
            case R.id.mc_create_final_frame: //DESCR
                if (accident.isAccident()) {
                    goToMed();
                    accident.resetMed();
                } else {
                    goToType();
                    accident.resetType();
                }
                break;
        }
        setConfirm();
        refreshDescription();
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

    private void confirm() {
        disableConfirm();
        CreateAccidentRequest request = new CreateAccidentRequest(new CreateAccidentCallback(), context);
        request.setType(accident.type);
        request.setMed(accident.med);
        request.setAddress(accident.address);
        request.setLocation(accident.location);
        request.setDescription(accident.description);
        request.setCreated(accident.created);
        request.execute();
    }

    private void goToAccidentSubType() {
        hideAll();
        accFrame.setVisibility(View.VISIBLE);
        currentScreen = ACC;
    }

    private void goToMed() {
        hideAll();
        medFrame.setVisibility(View.VISIBLE);
        currentScreen = MED;
    }

    private void goToDescription() {
        hideAll();
        descrFrame.setVisibility(View.VISIBLE);
        currentScreen = DESCR;
    }

    private void goToMap() {
        hideAll();
        mapFrame.setVisibility(View.VISIBLE);
        currentScreen = MAP;
        backButton.setEnabled(false);
    }

    private void goToType() {
        hideAll();
        typeFrame.setVisibility(View.VISIBLE);
        currentScreen = TYPE;
        backButton.setEnabled(true);
    }

    private void hideAll() {
        typeFrame.setVisibility(View.INVISIBLE);
        mapFrame.setVisibility(View.INVISIBLE);
        medFrame.setVisibility(View.INVISIBLE);
        descrFrame.setVisibility(View.INVISIBLE);
        accFrame.setVisibility(View.INVISIBLE);
    }

    private GoogleMap makeMap() {
        GoogleMap          map;
        FragmentManager    fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        SupportMapFragment mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mc_create_map_container);
        map = mapFragment.getMap();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(accident.location), 16));
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (!Role.isModerator()) {
            CircleOptions circleOptions = new CircleOptions().center(MyUtils.LocationToLatLng(accident.initialLocation)).radius(RADIUS).fillColor(0x20FF0000);
            map.addCircle(circleOptions);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition camera) {
                    Button mcCreateFineAddressConfirm = (Button) ((Activity) context).findViewById(R.id.mc_create_fine_address_confirm);
                    if (accident.initialLocation != null) {
                        double distance = MyUtils.LatLngToLocation(camera.target).distanceTo(accident.initialLocation);
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
        for (int id : AccidentsGeneral.points.keySet()) {
            Accident point = AccidentsGeneral.points.getPoint(id);
            if (point.isInvisible()) continue;
            String title = point.getTypeText();
            if (!point.getMedText().equals("")) {
                title += ", " + point.getMedText();
            }
            title += ", " + MyUtils.getIntervalFromNowInText(point.created) + " назад";

            float alpha;
            int age = (int) (((new Date()).getTime() - point.created.getTime()) / 3600000);
            if (age < 2) {
                alpha = 1.0f;
            } else if (age < 6) {
                alpha = 0.5f;
            } else {
                alpha = 0.2f;
            }
            map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(point.getLocation())).title(title)
                                             .icon(AccidentTypes.getBitmapDescriptor(point.getType())).alpha(alpha));
        }
        return map;
    }

    private void disableConfirm() {
        setConfirm(false);
        confirmLock = true;
    }

    private void enableConfirm() {
        confirmLock = false;
        setConfirm(true);
    }

    private void setConfirm(Boolean status) {
        if (confirmLock) return;
        confirmButton.setEnabled(status);
    }

    private void setConfirm() {
        setConfirm(accident.isComplete());
    }

    private class NewAccident {
        String   type;
        String   med;
        Location location;
        final Location initialLocation;
        final Date     created;
        String address;
        String description;

        public NewAccident() {
            type = "";
            med = "mc_m_na";
            initialLocation = location = MyLocationManager.getLocation(context);
            created = new Date();
            address = MyLocationManager.address;
            description = "";
        }

        public void makeBreak() {
            type = "acc_b";
        }

        public void makeSteal() {
            type = "acc_s";
        }

        public void makeOther() {
            type = "acc_o";
        }

        public void makeAccident(String type) {
            this.type = type;
        }

        public void setMed(String med) {
            this.med = med;
        }

        public void setDescription(String description) {
            this.description = description.replace("^\\s+", "");
            this.description = description.replace("\\s+$", "");
        }

        public void resetType() {
            type = "";
            med = "mc_m_na";
        }

        public void resetMed() {
            med = "mc_m_na";
        }

        public boolean isComplete() {
            if (isAccident()) {
                return true;
            } else if ((isSteal() || isBreak() || isOther()) && description.length() > 3) {
                return true;
            }
            return false;
        }

        public void updateLocation(Location location) {
            this.location = location;
            updateAddress(((MyApp) context.getApplicationContext()).getAddres(accident.location));
        }

        public boolean isSteal() {
            return type.equals("acc_s");
        }

        public boolean isBreak() {
            return type.equals("acc_b");
        }

        public boolean isOther() {
            return type.equals("acc_o");
        }

        public boolean isAccident() {
            return (type + "      ").substring(0, 5).equals("acc_m");
        }
    }

    private class CreateAccidentCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Неизвестная ошибка" + result.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                finish();
            }
            enableConfirm();
        }
    }
}
