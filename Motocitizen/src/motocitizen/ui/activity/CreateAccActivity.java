package motocitizen.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;

import org.json.JSONException;

import java.util.Date;

import kotlin.Unit;
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

public class CreateAccActivity extends FragmentActivity implements View.OnClickListener {
    /* constants */
    private static final int RADIUS      = 2000;
    private static final int TYPE        = R.id.create_type_frame;
    private static final int DESCRIPTION = R.id.create_final_frame;
    private static final int ACCIDENT    = R.id.create_acc_frame;
    private static final int MEDICINE    = R.id.create_people_frame;
    private static final int MAP         = R.id.create_map;
    /* end of constants */


    private GoogleMap map;
    private Button    confirmButton;
    private Location  initialLocation;
    private EditText  searchEditText;
    private boolean         complete      = false;
    private int             currentScreen = MAP;
    private boolean         confirmLock   = false;
    private AccidentBuilder ab            = new AccidentBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_point);
        initialLocation = MyLocationManager.getLocation();
        makeMap();
        confirmButton = (Button) findViewById(R.id.CREATE);
        EditText createFinalText = (EditText) findViewById(R.id.create_final_text);
        createFinalText.addTextChangedListener(new FinalTextWatcher());
        searchEditText = (EditText) findViewById(R.id.SearchEditText);

        setUpScreen(MAP);
        refreshDescription();
        setupListener();
    }

    @SuppressWarnings({"MissingPermission"})
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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ab.getCoordinates(), 16));
            enableMyLocation();
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            if (!User.INSTANCE.isModerator()) {
                //Прячем кнопки поиска адреса
                searchEditText.setVisibility(View.GONE);
                ImageButton searchButton = (ImageButton) findViewById(R.id.SEARCH);
                searchButton.setVisibility(View.GONE);

                CircleOptions circleOptions = new CircleOptions().center(LocationUtils.toLatLng(initialLocation)).radius(RADIUS).fillColor(0x20FF0000);
                map.addCircle(circleOptions);
                map.setOnCameraMoveCanceledListener(() -> {
                    Button mcCreateFineAddressConfirm = (Button) findViewById(R.id.ADDRESS);
                    mcCreateFineAddressConfirm.setEnabled(false);
                    if (initialLocation == null) return;
                    double distance = LocationUtils.distanceTo(map.getCameraPosition().target, initialLocation);
                    mcCreateFineAddressConfirm.setEnabled(distance < RADIUS);
                });
            }
            map.clear();
            //todo refactor
            for (Accident point : Content.INSTANCE.getVisible()) {
                String title = point.getType().getText();
                title += point.getMedicine() == Medicine.NO ? "" : ", " + point.getMedicine().getText();
                title += ", " + DateUtils.getIntervalFromNowInText(CreateAccActivity.this, point.getTime()) + " назад";

                float alpha;
                int   age = (int) (((new Date()).getTime() - point.getTime().getTime()) / 3600000);
                if (age < 2) {
                    alpha = 1.0f;
                } else if (age < 6) {
                    alpha = 0.5f;
                } else {
                    alpha = 0.2f;
                }
                map.addMarker(new MarkerOptions().position(point.getCoordinates()).title(title).icon(point.getType().getIcon()).alpha(alpha));
            }
        }
    }

    private void makeMap() {
        FragmentManager    fragmentManager = this.getSupportFragmentManager();
        SupportMapFragment mapFragment     = (SupportMapFragment) fragmentManager.findFragmentById(R.id.create_map_container);
        mapFragment.getMapAsync(new OnMapCreated());
    }

    private void setComplete() {
        complete = ab.build().isAccident() || ab.getDescription().length() > 6;
        setConfirm(isComplete());
    }

    private void setUpScreen(int id) {
        hideAll();
        findViewById(id).setVisibility(View.VISIBLE);
        currentScreen = id;
        findViewById(R.id.BACK).setEnabled(id != MAP);
    }

    private void refreshDescription() {
        String text = ab.getType().getText();
        text += ab.getMedicine() == Medicine.UNKNOWN ? "" : ". " + ab.getMedicine().getText();
        ((TextView) findViewById(R.id.create_what)).setText(text);
        ((TextView) findViewById(R.id.create_who)).setText(Preferences.INSTANCE.getLogin());
        ((TextView) findViewById(R.id.create_where)).setText(ab.getAddress());
        ((TextView) findViewById(R.id.create_when)).setText(DateUtils.dateTimeString(ab.getTime()));
    }

    private void setupListener() {
        Integer[] ids = {R.id.BREAK,
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
                         R.id.SEARCH};
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

    /**
     * Показ диалога для ввода адреса, когда он не был определён по координатам
     */
    public void showRatingDialog() {
        final AlertDialog.Builder addressDialog = new AlertDialog.Builder(this);
        addressDialog.setTitle(R.string.addressDialog);
        View linearlayout = getLayoutInflater().inflate(R.layout.dialog, null);
        addressDialog.setView(linearlayout);
        EditText addressEditText = (EditText) linearlayout.findViewById(R.id.address_edit_Text);
        addressDialog.setPositiveButton("Готово",
                                        (dialog, which) -> {
                                            String temp = addressEditText.getText().toString().replaceAll("\\s", "");
                                            if (temp.length() > 0) {
                                                ab.address(addressEditText.getText().toString());
                                                refreshDescription();
                                            }
                                        })
                     .setNegativeButton("Отмена", (dialog, id) -> {
                         dialog.cancel();
                     });
        addressDialog.create();
        addressDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.BREAK:
            case R.id.STEAL:
            case R.id.OTHER:
                ab.type(getSelectedType(id));
                setUpScreen(DESCRIPTION);
                break;
            case R.id.MOTO_AUTO:
            case R.id.SOLO:
            case R.id.MOTO_MOTO:
            case R.id.MOTO_MAN:
                ab.type(getSelectedType(id));
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
                ab.medicine(getSelectedMedicine(id));
                setUpScreen(DESCRIPTION);
                setComplete();
                break;
            case R.id.ADDRESS:
                ab.coordinates(map.getCameraPosition().target);
                ab.address(MyLocationManager.getInstance().getAddress(ab.getCoordinates()));
                setUpScreen(TYPE);
                if (ab.getAddress().equals("")) {
                    showRatingDialog();
                }
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
                LocationUtils.fromAddress(searchEditText.getText().toString(), latLng -> {
                    if (latLng == null) {
                        ToastUtils.show(CreateAccActivity.this, CreateAccActivity.this.getString(R.string.nothing_is_found));
                    } else {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    }
                    return Unit.INSTANCE;
                });
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

    //{"result":"ERROR PREREQUISITES"}
    private void confirm() {
        disableConfirm();
        new CreateAccidentRequest(ab.build(),
                                  response -> {
                                      try {
                                          if (response.has("result") && response.getJSONObject("result").has("ID")) {
                                              finish();
                                              return Unit.INSTANCE;
                                          }
                                      } catch (JSONException e) {
                                          e.printStackTrace();
                                      }
                                      String message = response.optString("error", response.optString("result", ""));
                                      switch (message) {
                                          case "AUTH ERROR":
                                              message = "Вы не авторизованы";
                                              break;
                                          case "NO RIGHTS":
                                          case "READONLY":
                                              message = "Недостаточно прав";
                                              break;
                                          case "PROBABLY SPAM":
                                              message = "Нельзя создавать события так часто";
                                              break;
                                          case "":
                                              message = "Неизвестная ошибка";
                                      }
                                      final String error = message;
                                      CreateAccActivity.this.runOnUiThread(() -> {
                                          ToastUtils.show(CreateAccActivity.this, error);
                                          enableConfirm();
                                      });
                                      return Unit.INSTANCE;
                                  },
                                  ((CheckBox) findViewById(R.id.forStat)).isChecked());
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
        this.complete = false;
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

    private class FinalTextWatcher implements TextWatcher {
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
    }
}
