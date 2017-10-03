package motocitizen.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import kotlin.Unit;
import motocitizen.content.accident.AccidentBuilder;
import motocitizen.datasources.network.ApiResponse;
import motocitizen.datasources.network.requests.CreateAccidentRequest;
import motocitizen.datasources.preferences.Preferences;
import motocitizen.dictionary.Medicine;
import motocitizen.dictionary.Type;
import motocitizen.geo.geocoder.AccidentLocation;
import motocitizen.geo.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.ui.dialogs.create.EmptyAddressDialog;
import motocitizen.ui.frames.FrameInterface;
import motocitizen.ui.frames.create.DamageFrame;
import motocitizen.ui.frames.create.DescriptionFrame;
import motocitizen.ui.frames.create.LocationFrame;
import motocitizen.ui.frames.create.SubTypeFrame;
import motocitizen.ui.frames.create.TypeFrame;
import motocitizen.utils.ContextUtilsKt;
import motocitizen.utils.DateUtils;
import motocitizen.utils.ToastUtils;

import static motocitizen.ui.activity.CreateAccActivity.Frames.DAMAGE;
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

    private static final int ROOT_LAYOUT = R.layout.create_point;

    private static final int WHAT  = R.id.create_what;
    private static final int WHO   = R.id.create_who;
    private static final int WHERE = R.id.create_where;
    private static final int WHEN  = R.id.create_when;

    private static final int BACK_BUTTON   = R.id.BACK;
    private static final int STAT_CHECKBOX = R.id.forStat;//todo move to frame

    private FrameInterface typeFrame;
    private FrameInterface subTypeFrame;
    private FrameInterface damageFrame;
    private FrameInterface locationFrame;
    private FrameInterface descriptionFrame;

    private TextView whatField;
    private TextView whoField;
    private TextView whereField;
    private TextView whenField;
    private CheckBox forStat;

    private Button backButton;

    private final AccidentBuilder builder = new AccidentBuilder();
    private       Frames          current = MAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ROOT_LAYOUT);

        bindViews();
        changeFrameTo(MAP);

        locationFrame.show(); //do not use changeFrameTo()

        setupListeners();
        refreshDescription();
    }

    private void bindViews() {
        backButton = (Button) findViewById(BACK_BUTTON);

        forStat = ((CheckBox) findViewById(STAT_CHECKBOX));

        whatField = (TextView) findViewById(WHAT);
        whoField = (TextView) findViewById(WHO);
        whereField = (TextView) findViewById(WHERE);
        whenField = (TextView) findViewById(WHEN);

        locationFrame = new LocationFrame(this, this::selectLocationCallback);
        typeFrame = new TypeFrame(this, this::selectTypeCallback);
        damageFrame = new DamageFrame(this, this::selectDamageCallback);
        subTypeFrame = new SubTypeFrame(this, this::selectSubTypeCallback);
        descriptionFrame = new DescriptionFrame(this, builder, this::selectDescriptionCallback);
    }

    private void setupListeners() {
        findViewById(R.id.CANCEL).setOnClickListener(v -> finish());
        findViewById(R.id.BACK).setOnClickListener(v -> backButtonPressed());
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent e) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            backButtonPressed();
            return true;
        }
        return super.onKeyUp(keycode, e);
    }

    private void backButtonPressed() {
        if (current == MAP) finish();
        changeFrameTo(getPrevFrame());
        refreshDescription();
    }

    private void changeFrameTo(Frames frame) {
        hideCurrentFrame();
        current = frame;
        getFrame(current).show();
        backButton.setEnabled(current != MAP);
        refreshDescription();
    }

    private void refreshDescription() {
        String medicine = builder.getMedicine() == Medicine.UNKNOWN ? "" : ". " + builder.getMedicine().getText();

        whatField.setText(String.format("%s%s", builder.getType().getText(), medicine));
        whoField.setText(Preferences.INSTANCE.getLogin());
        whereField.setText(builder.getLocation().getAddress());
        whenField.setText(DateUtils.dateTimeString(builder.getTime()));
    }

    private void hideCurrentFrame() {
        getFrame(current).hide();
    }

    private FrameInterface getFrame(Frames frame) {
        switch (frame) {
            case MAP:
                return locationFrame;
            case TYPE:
                return typeFrame;
            case SUB_TYPE:
                return subTypeFrame;
            case DAMAGE:
                return damageFrame;
            case DESCRIPTION:
                return descriptionFrame;
            default:
                return locationFrame;
        }
    }

    private Unit selectDescriptionCallback() {
        //        disableConfirm(); //todo
        new CreateAccidentRequest(builder.build(),
                                  this::createAccidentCallback,
                                  forStat.isChecked());//todo remove flag argument
        return Unit.INSTANCE;
    }

    private Unit selectLocationCallback(LatLng latLng) {
        builder.location(new AccidentLocation(MyLocationManager.INSTANCE.getAddress(latLng), latLng));
        changeFrameTo(TYPE);
        if (builder.getLocation().getAddress().equals("")) {
            new EmptyAddressDialog(this, this::addressDialogCallback);
        }
        return Unit.INSTANCE;
    }

    private Unit selectTypeCallback(Type type) {
        builder.type(type);
        changeFrameTo(type.isAccident() ? SUB_TYPE : DESCRIPTION);
        return Unit.INSTANCE;
    }

    private Unit selectSubTypeCallback(Type type) {
        builder.type(type);
        changeFrameTo(DAMAGE);
        return Unit.INSTANCE;
    }

    private Unit selectDamageCallback(Medicine medicine) {
        builder.medicine(medicine);
        changeFrameTo(DESCRIPTION);
        return Unit.INSTANCE;
    }

    private Unit addressDialogCallback(String address) {
        if (address.length() > 0) {
            builder.location(new AccidentLocation(address, builder.getLocation().getCoordinates()));
            refreshDescription();
        }
        return Unit.INSTANCE;
    }

    private Unit createAccidentCallback(ApiResponse response) {
        if (response.getResultObject().has("id")) {
            finish();
            return Unit.INSTANCE;
        }

        CreateAccActivity.this.runOnUiThread(() -> {
            ContextUtilsKt.showToast(CreateAccActivity.this, makeErrorMessage(response.getError().getText()));
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

    private Frames getPrevFrame() {
        switch (current) {
            case MAP:
                return MAP;
            case DAMAGE:
                return SUB_TYPE;
            case SUB_TYPE:
                return TYPE;
            case TYPE:
                return MAP;
            case DESCRIPTION:
                return builder.build().isAccident() ? DAMAGE : TYPE;
            default:
                return current;
        }
    }
}
