package motocitizen.app.mc;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.MCUtils;
import motocitizen.utils.Show;
import motocitizen.utils.Text;

class MCCreateAcc {
    private static final Activity act = (Activity) Startup.context;
    private static final Button back = (Button) act.findViewById(R.id.mc_create_back);
    private static final Button confirm = (Button) act.findViewById(R.id.mc_create_create);
    private static final Button cancel = (Button) act.findViewById(R.id.mc_create_cancel);
    private static final View includeArea = (View) act.findViewById(R.id.mc_create_main_frame);
    private static final View globalView = (View) act.findViewById(R.id.mc_create_main);
    private static final EditText details = (EditText) act.findViewById(R.id.mc_create_final_text);
    private static final int TYPE = R.id.mc_create_type_frame;
    private static final int FINAL = R.id.mc_create_final_frame;
    private static final int ACC = R.id.mc_create_acc_frame;
    private static final int PEOPLE = R.id.mc_create_people_frame;
    private static final int FINEADDRESS = R.id.mc_create_map;

    private static Date date;
    private static String globalText;
    private static String medText;
    private static String ownerText;
    private static String addressText;
    private static String timeText;
    private static String type, med;
    private static Location location;
    private static Location initialLocation;
    private static int CURRENT;
    private static boolean isAcc;
    private static GoogleMap map;
    private static Circle circle;
    private static int radius;
    private static final OnCameraChangeListener cameraListener = new OnCameraChangeListener() {

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
    private static final OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v) {
            back.setEnabled(true);
            confirm.setEnabled(true);
            med = "mc_m_na";
            int id = v.getId();
            if (id == R.id.mc_create_type_acc_button) {
                show(ACC);
                isAcc = true;
                confirm.setEnabled(false);
                globalText = "ДТП";
            } else if (id == R.id.mc_create_type_break_button) {
                globalText = "Поломка";
                type = "acc_b";
                show(FINAL);
            } else if (id == R.id.mc_create_type_steal_button) {
                globalText = "Угон";
                type = "acc_s";
                show(FINAL);
            } else if (id == R.id.mc_create_type_other_button) {
                globalText = "Прочее";
                type = "acc_o";
                show(FINAL);
            } else if (id == R.id.mc_create_acc_ma_button) {
                globalText = "ДТП мот/авто";
                type = "acc_m_a";
                show(PEOPLE);
            } else if (id == R.id.mc_create_acc_solo_button) {
                globalText = "ДТП один участник";
                type = "acc_m";
                show(PEOPLE);
            } else if (id == R.id.mc_create_acc_mm_button) {
                globalText = "ДТП мот/мот";
                type = "acc_m_m";
                show(PEOPLE);
            } else if (id == R.id.mc_create_acc_mp_button) {
                globalText = "Наезд на пешехода";
                type = "acc_m_p";
                show(PEOPLE);
            } else if (id == R.id.mc_create_people_ok_button) {
                medText = "Без травм.";
                med = "mc_m_wo";
                show(FINAL);
            } else if (id == R.id.mc_create_people_light_button) {
                medText = "Ушибы.";
                med = "mc_m_l";
                show(FINAL);
            } else if (id == R.id.mc_create_people_hard_button) {
                medText = "Тяжелый.";
                med = "mc_m_h";
                show(FINAL);
            } else if (id == R.id.mc_create_people_death_button) {
                medText = "Летальный.";
                med = "mc_m_d";
                show(FINAL);
            } else if (id == R.id.mc_create_people_na_button) {
                medText = "";
                med = "mc_m_na";
                show(FINAL);
            } else if (id == R.id.mc_create_fine_address_button) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(MCUtils.LocationToLatLng(location), 16));
                MCObjects.mcCreateMapPointer.setImageDrawable(MCAccTypes.getDrawable(Startup.context, type));
                show(FINEADDRESS);
            } else if (id == R.id.mc_create_fine_address_confirm) {
                double distance = MCUtils.LatLngToLocation(map.getCameraPosition().target).distanceTo(initialLocation);
                if (distance > radius)
                    return;
                location = MCUtils.LatLngToLocation(map.getCameraPosition().target);
                addressText = MCLocation.getAddress(location);
            }
            writeGlobal();
        }
    };
    private static final OnClickListener confirmListener = new Button.OnClickListener() {
        public void onClick(View v) {
            JSONObject json = new JSONCall("mcaccidents", "createAcc").request(createPOST());
            if (json.has("result")) {
                String result = "error";
                try {
                    result = json.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!result.equals("OK")) {
                    Log.d("CREATE ACC ERROR", json.toString());
                }
                exit();
                MCAccidents.refresh(v.getContext());
            }
            else {
              //TODO Перенести в ресурсы
                Toast.makeText(act, "Запрос не был отправлен, возможно нет связи.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private static final OnClickListener backListener = new Button.OnClickListener() {
        public void onClick(View v) {
            backButton();
        }
    };
    private static final OnClickListener cancelListener = new Button.OnClickListener() {
        public void onClick(View v) {
            exit();
        }
    };

    public static void init() {
        setListeners();
        Text.set(R.id.mc_create_final_text, "");
        date = new Date();
        globalText = "";
        medText = "mc_m_na";
        ownerText = Startup.prefsDef.getString("mc.login", "");
        addressText = MCLocation.address;
        timeText = Const.timeFormat.format((date).getTime());
        location = MCLocation.current;
        initialLocation = location;
        CURRENT = TYPE;
        isAcc = false;
        writeGlobal();
        show(CURRENT);
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

    private static void writeGlobal() {
        if (!medText.equals("mc_m_na")) {
            Text.set(globalView, R.id.mc_create_what, globalText + ". " + medText);
        } else {
            Text.set(globalView, R.id.mc_create_what, globalText);
        }
        Text.set(globalView, R.id.mc_create_who, ownerText);
        Text.set(globalView, R.id.mc_create_where, addressText);
        Text.set(globalView, R.id.mc_create_when, timeText);
    }

    private static void setListeners() {
        int[] buttons = {R.id.mc_create_acc_ma_button, R.id.mc_create_acc_solo_button, R.id.mc_create_acc_mm_button, R.id.mc_create_acc_mp_button,
                R.id.mc_create_people_death_button, R.id.mc_create_people_hard_button, R.id.mc_create_people_light_button,
                R.id.mc_create_people_ok_button, R.id.mc_create_type_acc_button, R.id.mc_create_type_break_button, R.id.mc_create_type_steal_button,
                R.id.mc_create_type_other_button, R.id.mc_create_people_na_button, R.id.mc_create_fine_address_button,
                R.id.mc_create_fine_address_confirm};
        for (int i : buttons) {
            Button button = (Button) act.findViewById(i);
            button.setOnClickListener(listener);
        }
        back.setOnClickListener(backListener);
        cancel.setOnClickListener(cancelListener);
        confirm.setOnClickListener(confirmListener);
    }

    private static void exit() {
        Show.show(R.id.main_frame_applications);
        Keyboard.hide(details);
    }

    private static void backButton() {
        confirm.setEnabled(false);
        if (CURRENT == TYPE) {
            exit();
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

    private static void show(int page) {
        CURRENT = page;
        Show.show(includeArea.getId(), page);
        if (page == FINAL) {
            details.requestFocus();
            Keyboard.show(details);
        } else {
            Keyboard.hide(details);
        }
    }

    private static Map<String, String> createPOST() {
        Map<String, String> POST = new HashMap<>();
        POST.put("owner_id", String.valueOf(MCAccidents.auth.id));
        POST.put("type", type);
        POST.put("med", med);
        POST.put("status", "acc_status_act");
        POST.put("lat", String.valueOf(location.getLatitude()));
        POST.put("lon", String.valueOf(location.getLongitude()));
        POST.put("created", Const.dateFormat.format(date));
        POST.put("address", addressText);
        POST.put("descr", Text.get(details.getId()) + "");
        POST.put("login", MCAccidents.auth.getLogin());
        POST.put("passhash", MCAccidents.auth.makePassHash());
        POST.put("calledMethod", "createAcc");
        return POST;
    }
}
