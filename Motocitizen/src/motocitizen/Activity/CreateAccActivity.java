package motocitizen.Activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccTypes;
import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCLocation;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.network.CreateAccidentRequest;
import motocitizen.network.GeoCodeNewRequest;
import motocitizen.network.JsonRequest;
import motocitizen.startup.MCPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.MCUtils;
import motocitizen.utils.Text;

@SuppressWarnings("FieldCanBeLocal")
public class CreateAccActivity extends FragmentActivity implements View.OnClickListener {
    private static View globalView;
    private static String addressText;
    private static Context context;
    private Button back;
    private Button confirm;
    private Button cancel;
    private Button typeOtherButton;
    private Button typeStealButton;
    private Button typeBreakButton;
    private Button typeAccButton;
    private Button accMmButton;
    private Button accMpButton;
    private Button accSoloButton;
    private Button accMaButton;
    private Button peopleDeathButton;
    private Button peopleHardButton;
    private Button peopleLightButton;
    private Button peopleNaButton;
    private Button peopleOkButton;
    private Button fineAddressButton;
    private Button fineAddressConfirm;
    private EditText details;
    private int TYPE;
    private int FINAL;
    private int ACC;
    private int PEOPLE;
    private int FINEADDRESS;
    private Date date;
    private String globalText;
    private String medText;
    private String ownerText;
    private String timeText;
    private String type;
    private String med = "mc_m_na";
    private Location location;
    private Location initialLocation;
    private int CURRENT;
    private boolean isAcc;
    private GoogleMap map;
    private Circle circle;
    private int radius;
    private final GoogleMap.OnCameraChangeListener cameraListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition camera) {
            Button mcCreateFineAddressConfirm = (Button) ((Activity) context).findViewById(R.id.mc_create_fine_address_confirm);
            if (initialLocation != null) {
                double distance = MCUtils.LatLngToLocation(camera.target).distanceTo(initialLocation);
                if (distance > radius) {
                    mcCreateFineAddressConfirm.setEnabled(false);
                } else {
                    mcCreateFineAddressConfirm.setEnabled(true);
                }
            } else {
                mcCreateFineAddressConfirm.setEnabled(false);
            }
        }
    };
    private MCPreferences prefs;

    public static void updateAddress(String address) {
        addressText = address;
        Text.set(context, globalView, R.id.mc_create_where, addressText);
    }

    private void backButton() {
        confirm.setEnabled(false);
        if (CURRENT == TYPE) {
            finish();
        } else if (CURRENT == FINAL) {
            if (isAcc) {
                med = "mc_m_na";
                medText = "";
                confirm.setEnabled(true);
                show(PEOPLE);
            } else {
                show(TYPE);
                globalText = "";
            }
        } else if (CURRENT == ACC) {
            show(TYPE);
            isAcc = false;
            globalText = "";
        } else if (CURRENT == PEOPLE) {
            show(ACC);
            medText = "";
            globalText = "ДТП";
            med = "mc_m_na";
        } else if (CURRENT == FINEADDRESS) {
            show(FINAL);
            confirm.setEnabled(true);
        }
        if (CURRENT == TYPE) {
            back.setEnabled(false);
        }
        writeGlobal();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        prefs = new MCPreferences(this);
        setContentView(R.layout.mc_app_create_point);

        back = (Button) findViewById(R.id.mc_create_back);
        back.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.mc_create_create);
        confirm.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.mc_create_cancel);
        cancel.setOnClickListener(this);

        fineAddressButton = (Button) findViewById(R.id.mc_create_fine_address_button);
        fineAddressButton.setOnClickListener(this);
        fineAddressConfirm = (Button) findViewById(R.id.mc_create_fine_address_confirm);
        fineAddressConfirm.setOnClickListener(this);

        typeOtherButton = (Button) findViewById(R.id.mc_create_type_other_button);
        typeOtherButton.setOnClickListener(this);
        typeStealButton = (Button) findViewById(R.id.mc_create_type_steal_button);
        typeStealButton.setOnClickListener(this);
        typeBreakButton = (Button) findViewById(R.id.mc_create_type_break_button);
        typeBreakButton.setOnClickListener(this);
        typeAccButton = (Button) findViewById(R.id.mc_create_type_acc_button);
        typeAccButton.setOnClickListener(this);

        accMmButton = (Button) findViewById(R.id.mc_create_acc_mm_button);
        accMmButton.setOnClickListener(this);

        accMpButton = (Button) findViewById(R.id.mc_create_acc_mp_button);
        accMpButton.setOnClickListener(this);
        accSoloButton = (Button) findViewById(R.id.mc_create_acc_solo_button);
        accSoloButton.setOnClickListener(this);
        accMaButton = (Button) findViewById(R.id.mc_create_acc_ma_button);
        accMaButton.setOnClickListener(this);

        peopleDeathButton = (Button) findViewById(R.id.mc_create_people_death_button);
        peopleDeathButton.setOnClickListener(this);
        peopleHardButton = (Button) findViewById(R.id.mc_create_people_hard_button);
        peopleHardButton.setOnClickListener(this);
        peopleLightButton = (Button) findViewById(R.id.mc_create_people_light_button);
        peopleLightButton.setOnClickListener(this);
        peopleNaButton = (Button) findViewById(R.id.mc_create_people_na_button);
        peopleNaButton.setOnClickListener(this);
        peopleOkButton = (Button) findViewById(R.id.mc_create_people_ok_button);
        peopleOkButton.setOnClickListener(this);

        globalView = findViewById(R.id.mc_create_main);
        details = (EditText) findViewById(R.id.mc_create_final_text);

        TYPE = R.id.mc_create_type_frame;
        FINAL = R.id.mc_create_final_frame;
        ACC = R.id.mc_create_acc_frame;
        PEOPLE = R.id.mc_create_people_frame;
        FINEADDRESS = R.id.mc_create_map;

        date = new Date();
        medText = "mc_m_na";
        ownerText = prefs.getLogin();
        addressText = MCLocation.address;
        timeText = Const.timeFormat.format((date).getTime());
        location = MCLocation.getLocation(context);
        initialLocation = location;
        CURRENT = TYPE;
        isAcc = false;
        writeGlobal();
        //show(CURRENT);

        //TODO Зачем это вообще все нужно, если юзер не будет корректировать адрес?
        //if (location != null) {
        //map = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.mc_create_map_container)).getMap();

        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        final SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mc_create_map_container);
        map = mapFragment.getMap();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MCUtils.LocationToLatLng(location), 16));
        // map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (!MCRole.isModerator()) {
            radius = 30000000;
        } else {
            radius = 1000;
            CircleOptions circleOptions = new CircleOptions().center(MCUtils.LocationToLatLng(initialLocation)).radius(radius).fillColor(0x20FF0000);
            if (circle != null) {
                circle.remove();
            }
            circle = map.addCircle(circleOptions);
        }
        map.setOnCameraChangeListener(cameraListener);
        //}
    }

    private void writeGlobal() {
        if (!medText.equals("mc_m_na")) {
            Text.set(this, globalView, R.id.mc_create_what, globalText + ". " + medText);
        } else {
            Text.set(this, globalView, R.id.mc_create_what, globalText);
        }
        Text.set(this, globalView, R.id.mc_create_who, ownerText);
        Text.set(this, globalView, R.id.mc_create_where, addressText);
        Text.set(this, globalView, R.id.mc_create_when, timeText);
    }

    private void show(int page) {
        //TODO Правильное решение http://android-er.blogspot.ru/2012/05/add-and-remove-view-dynamically.html
        CURRENT = page;
        FrameLayout frame = (FrameLayout) findViewById(R.id.mc_create_main_frame);

        for (int i = 0; i < frame.getChildCount(); i++) {
            View tmp = frame.getChildAt(i);
            if (tmp.getId() == page) {
                tmp.setVisibility(View.VISIBLE);
            } else {
                tmp.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void parseResponse(JSONObject json) {
        if (json.has("result")) {
            String result = "error";
            try {
                result = json.getString("result");
                if (result.contains("ID")) {
                    Toast.makeText(this, this.getString(R.string.send_success), Toast.LENGTH_LONG).show();
                    finish();
                } else if (result.equals("READONLY")) {
                    Toast.makeText(this, this.getString(R.string.not_have_rights_error), Toast.LENGTH_LONG).show();
                } else if (result.equals("PROBABLY SPAM")) {
                    Toast.makeText(this, this.getString(R.string.too_often_acts), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, this.getString(R.string.parce_error), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, this.getString(R.string.send_error), Toast.LENGTH_LONG).show();
        }
    }

    private void OnConfirm() {
        if (location != null) {
            if (Startup.isOnline()) {
                confirm.setEnabled(false);
                Map<String, String> post = createPOST();
                JsonRequest request = new JsonRequest("mcaccidents", "createAcc", post, "", true);
                if (request != null) {
                    (new CreateAccidentRequest(this)).execute(request);
                }
            } else {
                confirm.setEnabled(true);
                Toast.makeText(this, this.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, this.getString(R.string.position_not_available), Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> createPOST() {
        Map<String, String> POST = new HashMap<>();
        POST.put("owner_id", String.valueOf(MCAccidents.auth.getID()));
        POST.put("type", type);
        POST.put("med", med);
        POST.put("status", "acc_status_act");
        POST.put("lat", String.valueOf(location.getLatitude()));
        POST.put("lon", String.valueOf(location.getLongitude()));
        POST.put("created", Const.dateFormat.format(date));
        POST.put("address", addressText);
        POST.put("descr", details.getText().toString());
        POST.put("login", MCAccidents.auth.getLogin());
        POST.put("passhash", MCAccidents.auth.makePassHash());
        POST.put("calledMethod", "createAcc");
        return POST;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_create_acc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        back.setEnabled(true);
        confirm.setEnabled(true);
        int id = v.getId();
        Button btn = (Button) findViewById(id);

        //TODO switch case
        if (id == R.id.mc_create_type_acc_button) {
            show(ACC);
            isAcc = true;
            confirm.setEnabled(false);
            globalText = "ДТП";
        } else if (id == R.id.mc_create_type_break_button) {
            globalText = btn.getText().toString();
            type = "acc_b";
            show(FINAL);
        } else if (id == R.id.mc_create_type_steal_button) {
            globalText = btn.getText().toString();
            type = "acc_s";
            show(FINAL);
        } else if (id == R.id.mc_create_type_other_button) {
            globalText = btn.getText().toString();
            type = "acc_o";
            show(FINAL);
        } else if (id == R.id.mc_create_acc_ma_button) {
            globalText = "ДТП " + btn.getText().toString();
            type = "acc_m_a";
            show(PEOPLE);
        } else if (id == R.id.mc_create_acc_solo_button) {
            globalText = "ДТП " + btn.getText().toString();
            type = "acc_m";
            show(PEOPLE);
        } else if (id == R.id.mc_create_acc_mm_button) {
            globalText = "ДТП " + btn.getText().toString();
            type = "acc_m_m";
            show(PEOPLE);
        } else if (id == R.id.mc_create_acc_mp_button) {
            globalText = "ДТП " + btn.getText().toString();
            type = "acc_m_p";
            show(PEOPLE);
        } else if (id == R.id.mc_create_people_ok_button) {
            medText = btn.getText().toString();
            med = "mc_m_wo";
            show(FINAL);
        } else if (id == R.id.mc_create_people_light_button) {
            medText = btn.getText().toString();
            med = "mc_m_l";
            show(FINAL);
        } else if (id == R.id.mc_create_people_hard_button) {
            medText = btn.getText().toString();
            med = "mc_m_h";
            show(FINAL);
        } else if (id == R.id.mc_create_people_death_button) {
            globalText = btn.getText().toString();
            med = "mc_m_d";
            show(FINAL);
        } else if (id == R.id.mc_create_people_na_button) {
            medText = "";
            med = "mc_m_na";
            show(FINAL);
        } else if (id == R.id.mc_create_fine_address_button) {
            if (location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(MCUtils.LocationToLatLng(location), 16));
            }
            ImageView mapPointer = (ImageView) this.findViewById(R.id.mc_create_map_pointer);
            mapPointer.setImageDrawable(MCAccTypes.getDrawable(this, type));
            Keyboard.hide(details);
            show(FINEADDRESS);
        } else if (id == R.id.mc_create_fine_address_confirm) {
            //TODO Если вдруг initialLocation == null, но не получается взять координаты после тыка юзера, т.к. map.getCameraPosition() падает.
            double distance = MCUtils.LatLngToLocation(map.getCameraPosition().target).distanceTo(initialLocation);
            if (distance > radius)
                return;
            location = MCUtils.LatLngToLocation(map.getCameraPosition().target);
            getAddress(location);
        } else if (id == R.id.mc_create_back) {
            backButton();
        } else if (id == R.id.mc_create_cancel) {
            //TODO Добавить подтверждение
            finish();
        } else if (id == R.id.mc_create_create) {
            OnConfirm();
        }
        writeGlobal();
    }

    private void getAddress(Location location) {
        if (Startup.isOnline()) {
            JsonRequest request = getAddressRequest(location);
            if (request != null) {
                (new GeoCodeNewRequest(this)).execute(request);
            }
        } else {
            Toast.makeText(this, this.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
    }

    private JsonRequest getAddressRequest(Location location) {
        Map<String, String> post = new HashMap<>();
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        return new JsonRequest("mcaccidents", "geocode", post, "", true);
    }
}
