package motocitizen.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import motocitizen.content.accident.AccidentBuilder;
import motocitizen.datasources.network.requests.CreateAccidentRequest;
import motocitizen.dictionary.Medicine;
import motocitizen.dictionary.Type;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.maps.google.CreateAccidentMap;
import motocitizen.utils.DateUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.ToastUtils;

public class CreateAccActivity extends FragmentActivity {
    private static final int ROOT_LAYOUT   = R.layout.create_point;
    private static final int DIALOG_LAYOUT = R.layout.dialog;

    private static final int TYPE        = R.id.create_type_frame;
    private static final int DESCRIPTION = R.id.create_final_frame;
    private static final int ACCIDENT    = R.id.create_acc_frame;
    private static final int MEDICINE    = R.id.create_people_frame;
    private static final int MAP         = R.id.create_map;

    private static final int WHAT  = R.id.create_what;
    private static final int WHO   = R.id.create_who;
    private static final int WHERE = R.id.create_where;
    private static final int WHEN  = R.id.create_when;

    private static final int CREATE_BUTTON = R.id.CREATE;

    private static final int BACK_BUTTON   = R.id.BACK;
    private static final int STAT_CHECKBOX = R.id.forStat;

    /* end of constants */
    private CreateAccidentMap map;
    private Button            confirmButton;
    private TextView          whatField;
    private TextView          whoField;
    private TextView          whereField;
    private TextView          whenField;

    private boolean         complete      = false;
    private int             currentScreen = MAP;
    private boolean         confirmLock   = false;
    private AccidentBuilder ab            = new AccidentBuilder();

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

        whatField = (TextView) findViewById(WHAT);
        whoField = (TextView) findViewById(WHO);
        whereField = (TextView) findViewById(WHERE);
        whenField = (TextView) findViewById(WHEN);
    }

    private void makeMap() {
        map = new CreateAccidentMap(this);
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
            ab.coordinates(map.coordinates());
            ab.address(MyLocationManager.getInstance().getAddress(ab.getCoordinates()));
            setUpScreen(TYPE);
            if (ab.getAddress().equals("")) {
                showRatingDialog();
            }
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
