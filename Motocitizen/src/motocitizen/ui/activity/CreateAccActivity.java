package motocitizen.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.content.accident.AccidentBuilder;
import motocitizen.datasources.network.requests.CreateAccidentRequest;
import motocitizen.dictionary.Medicine;
import motocitizen.dictionary.Type;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.user.User;
import motocitizen.utils.DateUtils;
import motocitizen.utils.LocationUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.ToastUtils;

public class CreateAccActivity extends FragmentActivity {
    private static final int ROOT_LAYOUT   = R.layout.create_point;
    private static final int DIALOG_LAYOUT = R.layout.dialog;

    /* constants */
    private static final int MS_IN_HOUR              = 3600000;
    private static final int PERMITTED_REGION_COLOR  = 0x20FF0000;
    private static final int PERMITTED_REGION_RADIUS = 2000;
    private static final int DEFAULT_ZOOM            = 16;
    private static final int TYPE                    = R.id.create_type_frame;
    private static final int DESCRIPTION             = R.id.create_final_frame;
    private static final int ACCIDENT                = R.id.create_acc_frame;
    private static final int MEDICINE                = R.id.create_people_frame;
    private static final int MAP                     = R.id.create_map;

    private static final int MAP_CONTAINER = R.id.create_map_container;

    private static final int WHAT  = R.id.create_what;
    private static final int WHO   = R.id.create_who;
    private static final int WHERE = R.id.create_where;
    private static final int WHEN  = R.id.create_when;

    private static final int SEARCH_BUTTON  = R.id.SEARCH;
    private static final int ADDRESS_BUTTON = R.id.ADDRESS;
    private static final int CREATE_BUTTON  = R.id.CREATE;
    private static final int SEARCH_INPUT   = R.id.SearchEditText;
    private static final int BACK_BUTTON    = R.id.BACK;
    private static final int STAT_CHECKBOX  = R.id.forStat;

    /* end of constants */
    private GoogleMap map;
    private Button    confirmButton;
    private EditText  searchEditText;
    private TextView  whatField;
    private TextView  whoField;
    private TextView  whereField;
    private TextView  whenField;

    private Location        initialLocation = MyLocationManager.getLocation();
    private boolean         complete        = false;
    private int             currentScreen   = MAP;
    private boolean         confirmLock     = false;
    private AccidentBuilder ab              = new AccidentBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ROOT_LAYOUT);

        makeMap();
        bindViews();
        setUpScreen(MAP);
        setupListeners();
        refreshDescription();
    }

    private void bindViews() {
        confirmButton = (Button) findViewById(CREATE_BUTTON);
        searchEditText = (EditText) findViewById(SEARCH_INPUT);
        whatField = (TextView) findViewById(WHAT);
        whoField = (TextView) findViewById(WHO);
        whereField = (TextView) findViewById(WHERE);
        whenField = (TextView) findViewById(WHEN);
    }

    private void enableMyLocation() {
        Dexter.withActivity(this)
              .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(permissionListener())
              .check();
    }

    @SuppressWarnings({ "MissingPermission" })
    private BasePermissionListener permissionListener() {
        return new BasePermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                map.setMyLocationEnabled(true);
            }
        };
    }

    private OnMapReadyCallback mapReadyCallback() {
        return googleMap -> {
            map = googleMap;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ab.getCoordinates(), DEFAULT_ZOOM));
            setUpMapUi();
            placeAccidentsOnMap();
        };
    }

    private void setUpMapUi() {
        enableMyLocation();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        addMapConstraints();
    }

    private void addMapConstraints() {
        if (User.INSTANCE.isModerator()) return;

        //Прячем кнопки поиска адреса
        searchEditText.setVisibility(View.GONE);
        ImageButton searchButton = (ImageButton) findViewById(SEARCH_BUTTON);
        searchButton.setVisibility(View.GONE);

        map.addCircle(new CircleOptions()
                              .center(LocationUtils.toLatLng(initialLocation))
                              .radius(PERMITTED_REGION_RADIUS)
                              .fillColor(PERMITTED_REGION_COLOR));
        map.setOnCameraMoveCanceledListener(cameraMoveCanceledListener());
    }

    private GoogleMap.OnCameraMoveCanceledListener cameraMoveCanceledListener() {
        return () -> {
            Button mcCreateFineAddressConfirm = (Button) findViewById(ADDRESS_BUTTON);
            mcCreateFineAddressConfirm.setEnabled(false);
            if (initialLocation == null) return;
            double distance = LocationUtils.distanceTo(map.getCameraPosition().target, initialLocation);
            mcCreateFineAddressConfirm.setEnabled(distance < PERMITTED_REGION_RADIUS);
        };
    }

    private void placeAccidentsOnMap() {
        map.clear();
        for (Accident accident : Content.INSTANCE.getVisible()) {
            map.addMarker(makeMarker(accident));
        }
    }

    private MarkerOptions makeMarker(Accident accident) {
        return new MarkerOptions()
                .position(accident.getCoordinates())
                .title(makeTitle(accident))
                .icon(accident.getType().getIcon())
                .alpha(calculateAlpha(accident));
    }

    private String makeTitle(Accident accident) {
        String medicine = accident.getMedicine() == Medicine.NO ? "" : ", " + accident.getMedicine().getText();
        String interval = DateUtils.getIntervalFromNowInText(CreateAccActivity.this, accident.getTime());
        return String.format("%s%s, %s назад",
                             accident.getType().getText(),
                             medicine,
                             interval);
    }

    private float calculateAlpha(Accident accident) {
        int age = (int) (((new Date()).getTime() - accident.getTime().getTime()) / MS_IN_HOUR);
        if (age < 2) return 1.0f;
        if (age < 6) return 0.5f;
        return 0.2f;
    }

    private void makeMap() {
        FragmentManager    fragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(MAP_CONTAINER);
        mapFragment.getMapAsync(mapReadyCallback());
    }

    private void setComplete() {
        complete = ab.build().isAccident() || ab.getDescription().length() > 6;
        setConfirm(isComplete());
    }

    private void setUpScreen(int id) {
        hideAll();
        findViewById(id).setVisibility(View.VISIBLE);
        currentScreen = id;
        findViewById(BACK_BUTTON).setEnabled(id != MAP);
        refreshDescription();
    }

    private void refreshDescription() {
        String text = ab.getType().getText();
        text += ab.getMedicine() == Medicine.UNKNOWN ? "" : ". " + ab.getMedicine().getText();

        whatField.setText(text);
        whoField.setText(Preferences.INSTANCE.getLogin());
        whereField.setText(ab.getAddress());
        whenField.setText(DateUtils.dateTimeString(ab.getTime()));
    }

    private void setupListeners() {
        findViewById(R.id.ACCIDENT).setOnClickListener(v -> setUpScreen(ACCIDENT));

        findViewById(R.id.CREATE).setOnClickListener(v -> confirm());

        findViewById(R.id.CANCEL).setOnClickListener(v -> finish());

        findViewById(R.id.BACK).setOnClickListener(v -> backButton());

        findViewById(R.id.ADDRESS).setOnClickListener(addressSelectListener());

        findViewById(R.id.SEARCH).setOnClickListener(v -> LocationUtils.fromAddress(searchEditText.getText().toString(), searchCallback()));

        for (int id : new int[]{ R.id.MOTO_AUTO,
                                 R.id.SOLO,
                                 R.id.MOTO_MOTO,
                                 R.id.MOTO_MAN })
            findViewById(id).setOnClickListener(accidentTypeSelectListener(id));

        for (int id : new int[]{ R.id.BREAK,
                                 R.id.STEAL,
                                 R.id.OTHER })
            findViewById(id).setOnClickListener(otherTypeSelectListener(id));

        for (int id : new int[]{ R.id.PEOPLE_OK,
                                 R.id.PEOPLE_LIGHT,
                                 R.id.PEOPLE_HEAVY,
                                 R.id.PEOPLE_LETHAL,
                                 R.id.PEOPLE_UNKNOWN })
            findViewById(id).setOnClickListener(damageSelectListener(id));

        ((EditText) findViewById(R.id.create_final_text)).addTextChangedListener(finalTextWatcher());
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

    /**
     * Показ диалога для ввода адреса, когда он не был определён по координатам
     */
    public void showRatingDialog() {
        final AlertDialog.Builder addressDialog = new AlertDialog.Builder(this);
        addressDialog.setTitle(R.string.addressDialog);
        View linearLayout = getLayoutInflater().inflate(DIALOG_LAYOUT, null);
        addressDialog.setView(linearLayout);
        EditText addressEditText = (EditText) linearLayout.findViewById(R.id.address_edit_Text);
        addressDialog
                .setPositiveButton("Готово", dialogPositiveListener(addressEditText))
                .setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());
        addressDialog.create();
        addressDialog.show();
    }

    private Dialog.OnClickListener dialogPositiveListener(EditText addressEditText) {
        return (dialog, which) -> {
            String temp = addressEditText.getText().toString().replaceAll("\\s", "");
            if (temp.length() > 0) {
                ab.address(addressEditText.getText().toString());
                refreshDescription();
            }
        };
    }

    private View.OnClickListener otherTypeSelectListener(int id) {
        return v -> {
            ab.type(getSelectedType(id));
            setUpScreen(DESCRIPTION);
        };
    }

    private View.OnClickListener accidentTypeSelectListener(int id) {
        return v -> {
            ab.type(getSelectedType(id));
            setUpScreen(MEDICINE);
        };
    }

    private View.OnClickListener damageSelectListener(int id) {
        return v -> {
            ab.medicine(getSelectedMedicine(id));
            setUpScreen(DESCRIPTION);
            setComplete();
        };
    }

    private View.OnClickListener addressSelectListener() {
        return v -> {
            ab.coordinates(map.getCameraPosition().target);
            ab.address(MyLocationManager.getInstance().getAddress(ab.getCoordinates()));
            setUpScreen(TYPE);
            if (ab.getAddress().equals("")) {
                showRatingDialog();
            }
        };
    }

    private Function1<LatLng, Unit> searchCallback() {
        return latLng -> {
            if (latLng == null) {
                ToastUtils.show(CreateAccActivity.this, CreateAccActivity.this.getString(R.string.nothing_is_found));
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
            return Unit.INSTANCE;
        };
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
        new CreateAccidentRequest(ab.build(),
                                  createAccidentCallback(),
                                  ((CheckBox) findViewById(STAT_CHECKBOX)).isChecked());//todo remove flag argument
    }

    private Function1<JSONObject, Unit> createAccidentCallback() {
        return response -> {
            try {
                if (response.has("r") && response.getJSONObject("r").has("id")) {
                    finish();
                    return Unit.INSTANCE;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CreateAccActivity.this.runOnUiThread(() -> {
                ToastUtils.show(CreateAccActivity.this, makeErrorMessage(response.optString("e", response.optString("r", ""))));
                enableConfirm();
            });
            return Unit.INSTANCE;
        };
    }

    private String makeErrorMessage(String source) {
        switch (source) {
            case "AUTH ERROR":
                return "Вы не авторизованы";
            case "NO RIGHTS":
            case "READONLY":
                return "Недостаточно прав";
            case "PROBABLY SPAM":
                return "Нельзя создавать события так часто";
            default:
                return "Неизвестная ошибка";
        }
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
                setUpScreen(ab.build().isAccident() ? MEDICINE : TYPE);
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
        complete = false;
        setConfirm(false);
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK:
                backButton();
                return true;
            default:
                return super.onKeyUp(keycode, e);
        }
    }

    private void enableConfirm() {
        confirmLock = false;
        setConfirm(true);
    }

    private TextWatcher finalTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ab.description(s.toString());
                setComplete();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
}
