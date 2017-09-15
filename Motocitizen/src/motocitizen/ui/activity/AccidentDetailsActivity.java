package motocitizen.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import kotlin.Unit;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.content.accident.AccidentFactory;
import motocitizen.datasources.network.requests.ActivateAccident;
import motocitizen.datasources.network.requests.EndAccident;
import motocitizen.datasources.network.requests.HideAccident;
import motocitizen.dictionary.AccidentStatus;
import motocitizen.dictionary.Medicine;
import motocitizen.main.R;
import motocitizen.router.Router;
import motocitizen.ui.fragments.DetailHistoryFragment;
import motocitizen.ui.fragments.DetailMessagesFragment;
import motocitizen.ui.fragments.DetailVolunteersFragment;
import motocitizen.user.User;
import motocitizen.utils.DateUtils;
import motocitizen.utils.Utils;
import motocitizen.utils.popups.AccidentListPopup;

import static motocitizen.dictionary.AccidentStatus.ACTIVE;
import static motocitizen.dictionary.AccidentStatus.ENDED;

public class AccidentDetailsActivity extends AppCompatActivity {
    public static final String ACCIDENT_ID_KEY = "id";

    private static final int SMS_MENU_MIN_ID  = 100;
    private static final int SMS_MENU_MAX_ID  = 200;
    private static final int CALL_MENU_MIN_ID = 400;
    private static final int CALL_MENU_MAX_ID = 500;

    private static final int MENU                     = R.menu.menu_accident_details;
    private static final int ROOT_LAYOUT              = R.layout.activity_accident_details;
    private static final int GENERAL_INFORMATION_VIEW = R.id.acc_details_general;
    private static final int STATUS_VIEW              = R.id.acc_details_general_status;
    private static final int MEDICINE_VIEW            = R.id.acc_details_medicine;
    private static final int TYPE_VIEW                = R.id.acc_details_general_type;
    private static final int TIME_VIEW                = R.id.acc_details_general_time;
    private static final int OWNER_VIEW               = R.id.acc_details_general_owner;
    private static final int ADDRESS_VIEW             = R.id.acc_details_general_address;
    private static final int DISTANCE_VIEW            = R.id.acc_details_general_distance;
    private static final int DESCRIPTION_VIEW         = R.id.acc_details_general_description;
    private static final int FRAGMENT_ROOT_VIEW       = R.id.details_tab_content;
    private static final int TABS_GROUP               = R.id.details_tabs_group;
    private static final int MESSAGE_TAB              = R.id.details_tab_messages;
    private static final int HISTORY_TAB              = R.id.details_tab_history;
    private static final int VOLUNTEER_TAB            = R.id.details_tab_people;

    private Accident accident;

    private AccidentStatus accNewState;

    private DetailVolunteersFragment detailVolunteersFragment;
    private DetailMessagesFragment   detailMessagesFragment;
    private DetailHistoryFragment    detailHistoryFragment;

    private TextView   statusView;
    private TextView   medicineView;
    private View       generalLayout;
    private RadioGroup tabs;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accident = Content.INSTANCE.accident(getIntent().getExtras().getInt(ACCIDENT_ID_KEY));

        setContentView(ROOT_LAYOUT);

        detailVolunteersFragment = new DetailVolunteersFragment(accident);
        detailMessagesFragment = new DetailMessagesFragment(accident);
        detailHistoryFragment = new DetailHistoryFragment(accident);

        tabs = (RadioGroup) findViewById(TABS_GROUP);
        tabs.setVisibility(View.INVISIBLE);

        Content.INSTANCE.requestDetailsForAccident(accident, response -> {
            this.runOnUiThread(this::setupFragments);
            return Unit.INSTANCE;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        generalLayout = findViewById(GENERAL_INFORMATION_VIEW);
        statusView = (TextView) findViewById(STATUS_VIEW);
        medicineView = (TextView) findViewById(MEDICINE_VIEW);

        menuReconstruction();
    }

    private void setupFragments() {
        /*
        * Описание группы закладок внутри деталей происшествия
        */
        tabs.setVisibility(View.VISIBLE);
        tabs.setOnCheckedChangeListener((group, checkedId) -> {
            Fragment fragment;
            switch (group.getCheckedRadioButtonId()) {
                case MESSAGE_TAB:
                    fragment = detailMessagesFragment;
                    break;
                case HISTORY_TAB:
                    fragment = detailHistoryFragment;
                    break;
                case VOLUNTEER_TAB:
                    fragment = detailVolunteersFragment;
                    break;
                default:
                    return;
            }
            getFragmentManager().beginTransaction().replace(FRAGMENT_ROOT_VIEW, fragment).commit();
        });

        getFragmentManager().beginTransaction().replace(FRAGMENT_ROOT_VIEW, detailVolunteersFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        generalLayout.setOnLongClickListener(v -> {
            PopupWindow popupWindow;
            popupWindow = (new AccidentListPopup(AccidentDetailsActivity.this, accident.getId()))
                    .getPopupWindow(AccidentDetailsActivity.this);
            int viewLocation[] = new int[ 2 ];
            v.getLocationOnScreen(viewLocation);
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[ 0 ], viewLocation[ 1 ]);
            return true;
        });
        update();
    }

    public void update() {
        ActionBar actionBar = getSupportActionBar();
        //TODO Разобраться с nullPointerException и убрать костыль
        if (accident == null || actionBar == null) return;
        actionBar.setTitle(accident.getType().getText() + ". " + accident.distanceString());

        statusView.setVisibility(accident.getStatus() == ACTIVE ? View.GONE : View.VISIBLE);
        medicineView.setVisibility(accident.getMedicine() == Medicine.UNKNOWN ? View.GONE : View.VISIBLE);

        ((TextView) findViewById(TYPE_VIEW)).setText(accident.getType().getText());
        medicineView.setText("(" + accident.getMedicine().getText() + ")");
        statusView.setText(accident.getStatus().getText());
        ((TextView) findViewById(TIME_VIEW)).setText(DateUtils.getTime(accident.getTime()));
        ((TextView) findViewById(OWNER_VIEW)).setText(accident.ownerName());
        ((TextView) findViewById(ADDRESS_VIEW)).setText(accident.getAddress());
        ((TextView) findViewById(DISTANCE_VIEW)).setText(accident.distanceString());
        ((TextView) findViewById(DESCRIPTION_VIEW)).setText(accident.getDescription());

        menuReconstruction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(MENU, menu);
        mMenu = menu;
        //TODO Костыль
        if (accident == null) return super.onCreateOptionsMenu(menu);
        List<String> contactNumbers = Utils.getPhonesFromText(accident.getDescription());
        if (contactNumbers.isEmpty()) return super.onCreateOptionsMenu(menu);
        if (contactNumbers.size() == 1) {
            mMenu.add(0, SMS_MENU_MIN_ID, 0, getString(R.string.send_sms) + contactNumbers.get(0));
            mMenu.add(0, CALL_MENU_MIN_ID, 0, getString(R.string.make_call) + contactNumbers.get(0));
        } else {
            SubMenu smsSub  = mMenu.addSubMenu(getString(R.string.send_sms));
            SubMenu callSub = mMenu.addSubMenu(getString(R.string.make_call));
            for (int i = 0; i < contactNumbers.size(); i++) {
                smsSub.add(0, SMS_MENU_MIN_ID + i, 0, contactNumbers.get(i));
                callSub.add(0, CALL_MENU_MIN_ID + i, 0, contactNumbers.get(i));
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void menuReconstruction() {
        if (mMenu == null) return;
        MenuItem finish = mMenu.findItem(R.id.menu_acc_finish);
        MenuItem hide   = mMenu.findItem(R.id.menu_acc_hide);
        finish.setVisible(User.INSTANCE.isModerator());
        hide.setVisible(User.INSTANCE.isModerator());
        finish.setTitle(R.string.finish);
        hide.setTitle(R.string.hide);
        switch (accident.getStatus()) {
            case ENDED:
                finish.setTitle(R.string.unfinish);
                break;
            case HIDDEN:
                hide.setTitle(R.string.show);
        }
    }

    private void showGeneralLayout(int state) {
        MenuItem menuItemActionHideInfo = mMenu.findItem(R.id.action_hide_info);
        MenuItem menuItemMenuHideInfo   = mMenu.findItem(R.id.menu_hide_info);
        if (state == View.INVISIBLE) {
            generalLayout.setVisibility(View.GONE);
            menuItemActionHideInfo.setIcon(R.drawable.ic_panel_down);
            menuItemMenuHideInfo.setTitle(getString(R.string.show_info_details));
        } else {
            generalLayout.setVisibility(View.VISIBLE);
            menuItemActionHideInfo.setIcon(R.drawable.ic_panel_up);
            menuItemMenuHideInfo.setTitle(getString(R.string.hide_info_details));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
            case R.id.action_to_map:
                jumpToMap();
                return true;
            case R.id.action_share:
                //todo pornography
                Router.INSTANCE.share(this, AccidentListPopup.getAccidentTextToCopy(accident));
                return true;
            case R.id.action_hide_info:
            case R.id.menu_hide_info:
                if (generalLayout.getVisibility() == View.VISIBLE)
                    showGeneralLayout(View.INVISIBLE);
                else showGeneralLayout(View.VISIBLE);
                return true;
            case R.id.menu_acc_finish:
                sendFinishRequest();
                return true;
            case R.id.menu_acc_hide:
                sendHideRequest();
                return true;
        }
        if (item.getItemId() >= SMS_MENU_MIN_ID && item.getItemId() < SMS_MENU_MAX_ID) {
            String smsPrefix = getString(R.string.send_sms);
            String number    = (String) item.getTitle();
            if (number.contains(smsPrefix))
                number = number.substring(smsPrefix.length(), number.length());
            Router.INSTANCE.sms(this, number);
        } else if (item.getItemId() >= CALL_MENU_MIN_ID && item.getItemId() < CALL_MENU_MAX_ID) {
            String callPrefix = getString(R.string.make_call);
            String number     = (String) item.getTitle();
            if (number.contains(callPrefix))
                number = number.substring(callPrefix.length(), number.length());
            Router.INSTANCE.dial(this, number);
        }
        return false;
    }

    private void sendFinishRequest() {
        //TODO Суперкостыль !!!
        if (accident.getStatus() == ENDED) {
            new ActivateAccident(accident.getId(), this::accidentChangeCallback);
        } else {
            new EndAccident(accident.getId(), this::accidentChangeCallback);
        }
    }

    private void sendHideRequest() {
        //TODO какая то хуета
        if (accident.getStatus() == ENDED) {
            new ActivateAccident(accident.getId(), this::accidentChangeCallback);
            accNewState = ACTIVE;
        } else {
            new HideAccident(accident.getId(), this::accidentChangeCallback);
            accNewState = ENDED;
        }
    }

    private Unit accidentChangeCallback(JSONObject result) {
        if (result.has("error")) {
            //todo
        } else {
            //TODO Суперкостыль
            accident = AccidentFactory.INSTANCE.refactor(accident, accNewState);
            update();
        }
        return Unit.INSTANCE;
    }

    public void jumpToMap() {
        Bundle bundle = new Bundle();
        bundle.putInt("toMap", accident.getId());
        Router.INSTANCE.goTo(this, Router.Target.MAIN, bundle);
    }
}
