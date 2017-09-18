package motocitizen.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import motocitizen.content.accident.AccidentBuilder;
import motocitizen.datasources.network.requests.CreateAccidentRequest;
import motocitizen.dictionary.Medicine;
import motocitizen.dictionary.Type;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.ui.dialogs.SelectDamageFrame;
import motocitizen.ui.dialogs.SelectDescriptionFrame;
import motocitizen.ui.dialogs.SelectLocationFrame;
import motocitizen.ui.dialogs.SelectSubTypeFrame;
import motocitizen.ui.dialogs.SelectTypeFrame;
import motocitizen.utils.DateUtils;
import motocitizen.utils.Preferences;
import motocitizen.utils.ToastUtils;

import static motocitizen.ui.activity.CreateAccActivity.Frames.DESCRIPTION;
import static motocitizen.ui.activity.CreateAccActivity.Frames.MAP;
import static motocitizen.ui.activity.CreateAccActivity.Frames.SUB_TYPE;
import static motocitizen.ui.activity.CreateAccActivity.Frames.TYPE;

public class CreateAccActivity extends FragmentActivity {
    enum Frames {
        MAP,
        TYPE,
        SUB_TYPE,
        DAMAGE,
        DESCRIPTION
    }

    private Frames current = MAP;

    private static final int ROOT_LAYOUT   = R.layout.create_point;
    private static final int DIALOG_LAYOUT = R.layout.dialog;

    private static final int WHAT  = R.id.create_what;
    private static final int WHO   = R.id.create_who;
    private static final int WHERE = R.id.create_where;
    private static final int WHEN  = R.id.create_when;

    private static final int BACK_BUTTON   = R.id.BACK;
    private static final int STAT_CHECKBOX = R.id.forStat;//todo move to frame

    private SelectTypeFrame        selectTypeFrame;
    private SelectSubTypeFrame     selectSubTypeFrame;
    private SelectDamageFrame      selectDamageFrame;
    private SelectLocationFrame    selectLocationFrame;
    private SelectDescriptionFrame selectDescriptionFrame;

    private TextView whatField;
    private TextView whoField;
    private TextView whereField;
    private TextView whenField;

    private Button backButton;

    private AccidentBuilder builder = new AccidentBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ROOT_LAYOUT);

        bindViews();
        showFrame(MAP);
        selectLocationFrame.show();

        setupListeners();
        refreshDescription();
    }

    private void showFrame(Frames frame) {
        hideCurrentFrame();
        current = frame;
        switch (current) {
            case MAP: selectLocationFrame.show();
                break;
            case TYPE: selectTypeFrame.show();
                break;
            case SUB_TYPE: selectSubTypeFrame.show();
                break;
            case DAMAGE:
                selectDamageFrame.show();
                break;
            case DESCRIPTION: selectDescriptionFrame.show();
                break;
        }
        backButton.setEnabled(current != MAP);
        refreshDescription();
    }

    private void hideCurrentFrame() {
        switch (current) {
            case MAP: selectLocationFrame.hide();
                break;
            case TYPE: selectTypeFrame.hide();
                break;
            case SUB_TYPE: selectSubTypeFrame.hide();
                break;
            case DAMAGE:
                selectDamageFrame.hide();
                break;
            case DESCRIPTION: selectDescriptionFrame.hide();
                break;
        }
    }

    private void bindViews() {
        backButton = (Button) findViewById(BACK_BUTTON);

        whatField = (TextView) findViewById(WHAT);
        whoField = (TextView) findViewById(WHO);
        whereField = (TextView) findViewById(WHERE);
        whenField = (TextView) findViewById(WHEN);

        selectLocationFrame = new SelectLocationFrame(this, this::selectLocationCallback);
        selectTypeFrame = new SelectTypeFrame(this, this::selectTypeCallback);
        selectDamageFrame = new SelectDamageFrame(this, this::selectDamageCallback);
        selectSubTypeFrame = new SelectSubTypeFrame(this, this::selectSubTypeCallback);
        selectDescriptionFrame = new SelectDescriptionFrame(this, builder, this::selectDescriptionCallback);
    }

    private Unit selectDescriptionCallback() {
        confirm();
        return Unit.INSTANCE;
    }

    private Unit selectLocationCallback(LatLng latLng) {
        builder.coordinates(latLng);
        builder.address(MyLocationManager.getInstance().getAddress(latLng));
        showFrame(TYPE);
        if (builder.getAddress().equals("")) {
            showRatingDialog();
        }
        return Unit.INSTANCE;
    }

    private Unit selectTypeCallback(Type type) {
        builder.type(type);
        if (type.isAccident()) {
            showFrame(SUB_TYPE);
        } else {
            showFrame(DESCRIPTION);
        }
        return Unit.INSTANCE;
    }

    private Unit selectSubTypeCallback(Type type) {
        builder.type(type);
        showFrame(Frames.DAMAGE);
        return Unit.INSTANCE;
    }

    private Unit selectDamageCallback(Medicine medicine) {
        builder.medicine(medicine);
        showFrame(DESCRIPTION);
        return Unit.INSTANCE;
    }

    private void refreshDescription() {
        String text = builder.getType().getText();
        text += builder.getMedicine() == Medicine.UNKNOWN ? "" : ". " + builder.getMedicine().getText();

        whatField.setText(text);
        whoField.setText(Preferences.INSTANCE.getLogin());
        whereField.setText(builder.getAddress());
        whenField.setText(DateUtils.dateTimeString(builder.getTime()));
    }

    private void setupListeners() {
        findViewById(R.id.CANCEL).setOnClickListener(v -> finish());
        findViewById(R.id.BACK).setOnClickListener(v -> backButton());
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
                builder.address(addressEditText.getText().toString());
                refreshDescription();
            }
        };
    }

    private void confirm() {
//        disableConfirm(); //todo
        new CreateAccidentRequest(builder.build(),
                                  this::createAccidentCallback,
                                  ((CheckBox) findViewById(STAT_CHECKBOX)).isChecked());//todo remove flag argument
    }

    private Unit createAccidentCallback(JSONObject response) {
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
            //enableConfirm();//todo
        });
        return Unit.INSTANCE;
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
        switch (current) {
            case MAP:
                finish();
                break;
            case DAMAGE:
                showFrame(SUB_TYPE);
                break;
            case SUB_TYPE:
                showFrame(TYPE);
                break;
            case TYPE:
                showFrame(MAP);
                break;
            case DESCRIPTION:
                showFrame(builder.build().isAccident() ? Frames.DAMAGE : TYPE);
                break;
        }
        refreshDescription();
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
}
