package motocitizen.Activity;

import android.app.Activity;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
import motocitizen.app.mc.MCObjects;
import motocitizen.main.R;
import motocitizen.network.CreateAccidentRequest;
import motocitizen.network.GeoCodeNewRequest;
import motocitizen.network.GeoCodeRequest;
import motocitizen.network.JSONCall;
import motocitizen.network.JsonRequest;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.MCUtils;
import motocitizen.utils.Text;

public class CreateAccActivity extends ActionBarActivity implements View.OnClickListener {
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

    private static View globalView;
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
    private static String addressText;
    private String timeText;
    private String type, med;
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
            double distance = MCUtils.LatLngToLocation(camera.target).distanceTo(initialLocation);
            if (distance > radius) {
                MCObjects.mcCreateFineAddressConfirm.setEnabled(false);
            } else {
                MCObjects.mcCreateFineAddressConfirm.setEnabled(true);
            }
        }
    };

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
        setContentView(R.layout.mc_app_create_point);

        back = (Button) findViewById(R.id.mc_create_back);
        back.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.mc_create_create);
        confirm.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.mc_create_cancel);
        cancel.setOnClickListener(this);

        fineAddressButton = (Button) findViewById(R.id.mc_create_fine_address_button);
        fineAddressButton.setOnClickListener(this);
        fineAddressConfirm = (Button)findViewById(R.id.mc_create_fine_address_confirm);
        fineAddressConfirm.setOnClickListener(this);

        typeOtherButton = (Button) findViewById(R.id.mc_create_type_other_button);
        typeOtherButton.setOnClickListener(this);
        typeStealButton = (Button) findViewById(R.id.mc_create_type_steal_button);
        typeStealButton.setOnClickListener(this);
        typeBreakButton = (Button) findViewById(R.id.mc_create_type_break_button);
        typeBreakButton.setOnClickListener(this);
        typeAccButton = (Button) findViewById(R.id.mc_create_type_acc_button);
        typeAccButton.setOnClickListener(this);

        accMmButton = (Button)findViewById(R.id.mc_create_acc_mm_button);
        accMmButton.setOnClickListener(this);

        accMpButton = (Button)findViewById(R.id.mc_create_acc_mp_button);
        accMpButton.setOnClickListener(this);
        accSoloButton= (Button)findViewById(R.id.mc_create_acc_solo_button);
        accSoloButton.setOnClickListener(this);
        accMaButton= (Button)findViewById(R.id.mc_create_acc_ma_button);
        accMaButton.setOnClickListener(this);

        peopleDeathButton= (Button)findViewById(R.id.mc_create_people_death_button);
        peopleDeathButton.setOnClickListener(this);
        peopleHardButton= (Button)findViewById(R.id.mc_create_people_hard_button);
        peopleHardButton.setOnClickListener(this);
        peopleLightButton= (Button)findViewById(R.id.mc_create_people_light_button);
        peopleLightButton.setOnClickListener(this);
        peopleNaButton= (Button)findViewById(R.id.mc_create_people_na_button);
        peopleNaButton.setOnClickListener(this);
        peopleOkButton = (Button)findViewById(R.id.mc_create_people_ok_button);
        peopleOkButton.setOnClickListener(this);

        globalView = (View) findViewById(R.id.mc_create_main);
        details = (EditText) findViewById(R.id.mc_create_final_text);

        TYPE = R.id.mc_create_type_frame;
        FINAL = R.id.mc_create_final_frame;
        ACC = R.id.mc_create_acc_frame;
        PEOPLE = R.id.mc_create_people_frame;
        FINEADDRESS = R.id.mc_create_map;

        date = new Date();
        medText = "mc_m_na";
        ownerText = Startup.prefs.getString("mc.login", "");
        addressText = MCLocation.address;
        timeText = Const.timeFormat.format((date).getTime());
        location = MCLocation.current;
        initialLocation = location;
        CURRENT = TYPE;
        isAcc = false;
        writeGlobal();
        //show(CURRENT);

        map = ((MapFragment) ((Activity) Startup.context).getFragmentManager().findFragmentById(R.id.mc_create_map_container)).getMap();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MCUtils.LocationToLatLng(location), 16));
        // map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        // if (!MCRole.isModerator()) {
        // radius = 30000000;
        // } else {
        radius = 1000;
        CircleOptions circleOptions = new CircleOptions().center(MCUtils.LocationToLatLng(initialLocation)).radius(radius).fillColor(0x20FF0000);
        if (circle != null) {
            circle.remove();
        }
        circle = map.addCircle(circleOptions);
        // }
        map.setOnCameraChangeListener(cameraListener);
    }

    private void writeGlobal() {
        if (!medText.equals("mc_m_na")) {
            Text.set(globalView, R.id.mc_create_what, globalText + ". " + medText);
        } else {
            Text.set(globalView, R.id.mc_create_what, globalText);
        }
        Text.set(globalView, R.id.mc_create_who, ownerText);
        Text.set(globalView, R.id.mc_create_where, addressText);
        Text.set(globalView, R.id.mc_create_when, timeText);
    }

    public static void updateAddress(String address) {
        addressText = address;
        Text.set(globalView, R.id.mc_create_where, addressText);
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
                Toast.makeText(Startup.context, Startup.context.getString(R.string.send_succsess), Toast.LENGTH_LONG).show();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!result.equals("OK")) {
                Log.d("CREATE ACC ERROR", json.toString());
            }
        } else {
            Toast.makeText(Startup.context, Startup.context.getString(R.string.send_error), Toast.LENGTH_LONG).show();
        }
    }

    private void OnConfirm() {
        if (Startup.isOnline()) {
            Map<String, String> post = createPOST();
            JsonRequest request = new JsonRequest("mcaccidents", "createAcc", post, "", true);
            if (request != null) {
                (new CreateAccidentRequest(this)).execute(request);
            }
        } else {
            Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> createPOST() {
        Map<String, String> POST = new HashMap<>();
        POST.put("owner_id", String.valueOf(MCAccidents.auth.id));
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
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        back.setEnabled(true);
        confirm.setEnabled(true);
        med = "mc_m_na";
        int id = v.getId();
        Button btn = (Button)findViewById(id);

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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(MCUtils.LocationToLatLng(location), 16));
            MCObjects.mcCreateMapPointer.setImageDrawable(MCAccTypes.getDrawable(Startup.context, type));
            Keyboard.hide(details);
            show(FINEADDRESS);
        } else if (id == R.id.mc_create_fine_address_confirm) {
            double distance = MCUtils.LatLngToLocation(map.getCameraPosition().target).distanceTo(initialLocation);
            if (distance > radius)
                return;
            location = MCUtils.LatLngToLocation(map.getCameraPosition().target);
            // zz Пока не заработает у меня карта Google
            //LatLng latlng = new LatLng(55.624713, 38.079641);
            //location = MCUtils.LatLngToLocation(latlng);
            getAddress(location);
        } else if (id == R.id.mc_create_back) {
            backButton();
        } else if (id == R.id.mc_create_cancel) {
            //TODO Добавить подтверждение
            finish();
        } else if(id == R.id.mc_create_create) {
            OnConfirm();
        }
        writeGlobal();
    }

    private void getAddress(Location location) {
        if (Startup.isOnline()) {
            JsonRequest request = getAddressRequest(location);
            if (request != null) {
                (new GeoCodeNewRequest()).execute(request);
            }
        } else {
            Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
    }

    private JsonRequest getAddressRequest(Location location) {
        Map<String, String> post = new HashMap<>();
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));

        JsonRequest res = new JsonRequest("mcaccidents", "geocode", post, "", true);
        return res;
    }
}
