package motocitizen.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.network.AccidentFinishRequest;
import motocitizen.network.AccidentHideRequest;
import motocitizen.network.JSONCall;
import motocitizen.network.JsonRequest;
import motocitizen.startup.MCPreferences;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

public class AccidentDetailsActivity
        extends ActionBarActivity
        implements DetailMessagesFragment.OnFragmentInteractionListener,
        DetailHistoryFragment.OnFragmentInteractionListener,
        DetailVolunteersFragment.OnFragmentInteractionListener {

    /*
    Инцидент с которым работаем
     */
    private int accidentID;

    DetailVolunteersFragment detailVolunteersFragment;
    DetailMessagesFragment detailMessagesFragment;
    DetailHistoryFragment detailHistoryFragment;

    //private Button onwayButton;
    //private Button newMessageButton;
    //private EditText mcNewMessageText;
/*
    private View detMessages;
    private View detHistory;
    private View detVolunteers;
*/
    private View mcDetMessagesTable;
    private View onwayContent;
    private View inplaceContent;
    private View mcDetLogContent;

    private TextView generalType;
    private TextView generalStatus;
    private TextView generalTime;
    private TextView generalOwner;
    private TextView generalAddress;
    private TextView generalDescription;
    private View generalLayout;

    private Menu mMenu;

    MCPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        accidentID = b.getInt("accidentID");

        detailVolunteersFragment = detailVolunteersFragment.newInstance(accidentID);
        detailMessagesFragment = detailMessagesFragment.newInstance(accidentID);
        detailHistoryFragment = detailHistoryFragment.newInstance(accidentID);

//        newMessageButton = (Button) findViewById(R.id.mc_new_message_send);
//        newMessageButton.setOnClickListener(this);
//
//        mcNewMessageText = (EditText) findViewById(R.id.mc_new_message_text);
//        mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);

//        onwayButton = (Button) findViewById(R.id.onway_button);
//        onwayButton.setOnClickListener(this);
//
//        mcDetMessagesTable = findViewById(R.id.mc_det_messages_table);
//        onwayContent = findViewById(R.id.acc_onway_table);
//        inplaceContent = findViewById(R.id.acc_inplace_table);
//        mcDetLogContent = findViewById(R.id.mc_det_log_content);

//        Button toMapButton = (Button) findViewById(R.id.details_to_map_button);
//        toMapButton.setOnClickListener(this);
        /*
        * Описание группы закладок внутри деталей происшествия
        */
        RadioGroup mcDetTabsGroup = (RadioGroup) findViewById(R.id.mc_det_tabs_group);
        mcDetTabsGroup.setOnCheckedChangeListener(accDetTabsListener);
/*
        detMessages = findViewById(R.id.det_messages);
        detHistory = findViewById(R.id.det_history);
        detVolunteers = findViewById(R.id.det_volunteers);
*/
        generalLayout = findViewById(R.id.mc_acc_details_general);
        generalLayout.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                PopupWindow pw;
                pw = MCAccListPopup.getPopupWindow(MCAccidents.getCurrentPointID(), true);
                pw.showAsDropDown(v, 20, -20);
                return true;
            }
        });

        generalType = (TextView) findViewById(R.id.acc_details_general_type);
        generalStatus = (TextView) findViewById(R.id.acc_details_general_status);
        generalTime = (TextView) findViewById(R.id.acc_details_general_time);
        generalOwner = (TextView) findViewById(R.id.acc_details_general_owner);
        generalAddress = (TextView) findViewById(R.id.acc_details_general_address);
        generalDescription = (TextView) findViewById(R.id.acc_details_general_description);
//        ((ScrollView) findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);

        getFragmentManager().beginTransaction().replace(R.id.mc_det_tab_content, detailVolunteersFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO Вероятно теперь ни когда null не будет.
        if(prefs == null)
            prefs = new MCPreferences(this);
        update();
    }

    private void update() {
        MCPoint currentPoint = MCAccidents.points.getPoint(accidentID);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(currentPoint.getTypeText() + ". " + currentPoint.getDistanceText());

        generalType.setText(currentPoint.getTypeText() + ". " + currentPoint.getMedText());
        generalStatus.setText(currentPoint.getStatusText());
        generalTime.setText(Const.timeFormat.format(currentPoint.created.getTime()));
        generalOwner.setText(currentPoint.getOwner());
        generalAddress.setText("(" + currentPoint.getDistanceText() + ") " + currentPoint.getAddress());
        generalDescription.setText(currentPoint.getDescription());

        menuReconstriction();
/*
        if (currentPoint.id == prefs.getOnWay() || currentPoint.id == MCAccidents.getInplaceID()) {
            onwayButton.setVisibility(View.INVISIBLE);
        } else {
            onwayButton.setVisibility(View.VISIBLE);
        }
*/
        /*
         * Выводим список сообщений
		 */

/*
        findViewById(R.id.mc_acc_details_general).setOnLongClickListener(detLongClick);
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        for (int i : currentPoint.getSortedMessagesKeys()) {
            messageView.addView(currentPoint.messages.get(i).createRow(this));
        }
*/
        /*
         * Выводим список волонтеров
         */
        /*
        ViewGroup vg_onway = (ViewGroup) onwayContent;
        ViewGroup vg_inplace = (ViewGroup) inplaceContent;
        vg_onway.setVisibility(View.INVISIBLE);
        vg_inplace.setVisibility(View.INVISIBLE);
        vg_onway.removeAllViews();
        vg_inplace.removeAllViews();
        for (int i : currentPoint.getSortedVolunteersKeys()) {
            MCVolunteer current = currentPoint.volunteers.get(i);
            if (current.status.equals("onway")) {
                if (vg_onway.getVisibility() == View.INVISIBLE) {
                    vg_onway.setVisibility(View.VISIBLE);
                    vg_onway.addView(getDelimiterRow(this, "В пути"));
                }
                vg_onway.addView(current.createRow(this));
            }
        }
*/
        /*
         * Выводим историю
		 */
        /*
        ViewGroup logView = (ViewGroup) mcDetLogContent;
        logView.removeAllViews();
        logView.addView(MCPointHistory.createHeader(this));
        for (int i : currentPoint.getSortedHistoryKeys()) {
            logView.addView(currentPoint.history.get(i).createRow(this));
        }
*/
        Startup.map.zoom(16);
        Startup.map.jumpToPoint(currentPoint.getLocation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accident_details, menu);
        mMenu = menu;
        menuReconstriction();
        return super.onCreateOptionsMenu(menu);
    }

    private void menuReconstriction() {
        if(mMenu != null) {
            MCPoint currentPoint = MCAccidents.points.getPoint(accidentID);
            MenuItem finish = mMenu.findItem(R.id.menu_acc_finish);
            MenuItem hide = mMenu.findItem(R.id.menu_acc_hide);
            if (MCRole.isModerator()) {

                finish.setVisible(true);
                if (currentPoint.getStatus().equals("acc_status_end")) {
                    finish.setTitle(R.string.unfinish);
                } else {
                    finish.setTitle(R.string.finish);
                }

                hide.setVisible(true);
                if (currentPoint.getStatus().equals("acc_status_hide")) {
                    hide.setTitle(R.string.show);
                } else {
                    hide.setTitle(R.string.hide);
                }

            } else {
                finish.setVisible(false);
                hide.setVisible(false);
            }
        }
    }

    private void showGeneralLayout(int state) {
        MenuItem menuItemActionHideInfo = mMenu.findItem(R.id.action_hide_info);
        MenuItem menuItemMenuHideInfo = mMenu.findItem(R.id.menu_hide_info);
        if(state == View.INVISIBLE ) {
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
                jumpToMap();
                return true;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, MCAccidents.points.getTextToCopy(accidentID));
                sendIntent.setType("text/plain");
                this.startActivity(sendIntent);
                return true;
            case R.id.action_hide_info:
            case R.id.menu_hide_info:
                if(generalLayout.getVisibility() == View.VISIBLE)
                    showGeneralLayout(View.INVISIBLE);
                else
                    showGeneralLayout(View.VISIBLE);
                return true;
            case R.id.menu_acc_finish:
                sendFinishRequest();
                return true;
            case R.id.menu_acc_hide:
                sendHideRequest();
                return true;
        }
        return false;
    }

    private void sendFinishRequest() {
        if (Startup.isOnline()) {
            Map<String, String> params = new HashMap<>();
            params.put("login", MCAccidents.auth.getLogin());
            params.put("passhash", MCAccidents.auth.makePassHash());
            MCPoint point = MCAccidents.points.getPoint(accidentID);
            if (point.getStatus().equals("acc_status_end")) {
                params.put("state", "acc_status_act");
            } else {
                params.put("state", "acc_status_end");
            }
            params.put("id", String.valueOf(point.getId()));
            JsonRequest request = new JsonRequest("mcaccidents", "changeState", params, "", true);
            if (request != null) {
                (new AccidentFinishRequest(this, accidentID)).execute(request);
            }
        } else {
            Toast.makeText(this, getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
    }

    private void sendHideRequest() {
        if (Startup.isOnline()) {
            MCPoint point = MCAccidents.points.getPoint(accidentID);
            Map<String, String> params = new HashMap<>();
            params.put("login", MCAccidents.auth.getLogin());
            params.put("passhash", MCAccidents.auth.makePassHash());
            if (point.getStatus().equals("acc_status_hide")) {
                params.put("state", "acc_status_act");
            } else {
                params.put("state", "acc_status_hide");
            }
            params.put("id", String.valueOf(point.getId()));
            JsonRequest request = new JsonRequest("mcaccidents", "changeState", params, "", true);
            if (request != null) {
                (new AccidentHideRequest(this, accidentID)).execute(request);
            }
        } else {
            Toast.makeText(this, getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
    }

//    private void setupAccess() {
//        View newMessageArea = findViewById(R.id.mc_new_message_area);
//
//        if (MCRole.isStandart()) {
//            newMessageArea.setVisibility(View.VISIBLE);
//        } else {
//            newMessageArea.setVisibility(View.INVISIBLE);
//        }
//    }

    private final RadioGroup.OnCheckedChangeListener accDetTabsListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();

            if (id == R.id.mc_det_tab_messages) {
                getFragmentManager().beginTransaction().replace(R.id.mc_det_tab_content, detailMessagesFragment).commit();
            } else if (id == R.id.mc_det_tab_history) {
                getFragmentManager().beginTransaction().replace(R.id.mc_det_tab_content, detailHistoryFragment).commit();
            } else if (id == R.id.mc_det_tab_people) {
                getFragmentManager().beginTransaction().replace(R.id.mc_det_tab_content, detailVolunteersFragment).commit();
            }
        }
    };

    public void parseFinishResponse(JSONObject json, int currentId) {
        if (json.has("result")) {
            try {
                String result = json.getString("result");
                if (result.equals("OK")) {
                    Toast.makeText(this, Startup.context.getString(R.string.send_success), Toast.LENGTH_LONG).show();
                    MCAccidents.refresh(Startup.context);
                    menuReconstriction();
                    detailHistoryFragment.notifyDataSetChanged();
                    return;
                } else if (result.equals("READONLY") || result.equals("NO RIGHTS") ) {
                    Toast.makeText(this, this.getString(R.string.not_have_rights_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, this.getString(R.string.parce_error), Toast.LENGTH_LONG).show();
            }
            Log.e("Send message failed", json.toString());
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.message_send_error), Toast.LENGTH_LONG).show();
        }
    }

    public void parseSendMessageResponse(JSONObject json, int currentId) {
        if (json.has("result")) {
            try {
                String result = json.getString("result");
                if (result.equals("OK")) {
                    Toast.makeText(this, Startup.context.getString(R.string.send_success), Toast.LENGTH_LONG).show();
                    MCAccidents.refresh(Startup.context);
                    detailMessagesFragment.notifyDataSetChanged();
                    //mcNewMessageText.setText("");
                    //Keyboard.hide(findViewById(R.id.mc_new_message_text));
                    return;
                } else if (result.equals("READONLY")) {
                    Toast.makeText(this, this.getString(R.string.not_have_rights_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, this.getString(R.string.parce_error), Toast.LENGTH_LONG).show();
            }
            Log.e("Send message failed", json.toString());
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.message_send_error), Toast.LENGTH_LONG).show();
        }
    }

    public void parseOnwayResponse(JSONObject json, int currentId) {
        if (json.has("result")) {
            try {
                String result = json.getString("result");
                if (result.equals("OK")) {
                    Toast.makeText(this, Startup.context.getString(R.string.send_success), Toast.LENGTH_LONG).show();
                    prefs.setOnWay(currentId);
                    MCAccidents.refresh(Startup.context);
                    update();
                    detailVolunteersFragment.notifyDataSetChanged();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Set onway failed", json.toString());
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.send_error), Toast.LENGTH_LONG).show();
        }
    }
/*
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.onway_button:
                OnWayButton();
                break;
            case R.id.details_to_map_button:
                jumpToMap();
                break;
            default:
                Log.e("AccidentDetailsActivity", "Unknown button pressed");
                break;
        }
    }
*/
//    public void OnNewMessageSendButton(View v) {
//        detailMessagesFragment.myClickMethod(v);
//    }

    void jumpToMap() {
        Intent intent = new Intent(this, Startup.class);
        intent.putExtra("toMap", MCAccidents.getCurrentPointID());
        intent.putExtra("fromDetails", true);
        this.startActivity(intent);
    }

    /*
        void OnNewMessageSendButton() {
            if (Startup.isOnline()) {
                String text = mcNewMessageText.getText().toString();
                int currentId = MCAccidents.currentPoint.id;
                Map<String, String> post = new HashMap<>();
                post.put("login", prefs.getLogin());
                post.put("passhash", MCAccidents.auth.makePassHash());
                post.put("id", String.valueOf(currentId));
                post.put("text", text);
                JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
                if (request != null) {
                    (new SendMessageRequest(this, currentId)).execute(request);
                }
            } else {
                Toast.makeText(this, Startup.context.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
            }
        }

        void OnWayButton() {
            if (Startup.isOnline()) {
                int currentId = MCAccidents.currentPoint.id;
                Map<String, String> post = new HashMap<>();
                post.put("login", prefs.getLogin());
                post.put("passhash", MCAccidents.auth.makePassHash());
                post.put("id", String.valueOf(currentId));
                JsonRequest request = new JsonRequest("mcaccidents", "onway", post, "", true);
                if (request != null) {
                    (new OnwayRequest(this, currentId)).execute(request);
                }
            } else {
                Toast.makeText(this, Startup.context.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
            }
        }
    */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    MCPreferences getPref() {
        if(prefs == null)
            prefs = new MCPreferences(this);
        return prefs;
    }
}
