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

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.main.R;
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
        generalOwner.setText(currentPoint.owner);
        generalAddress.setText("(" + currentPoint.getDistanceText() + ") " + currentPoint.address);
        generalDescription.setText(currentPoint.descr);
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
        Startup.map.jumpToPoint(currentPoint.location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accident_details, menu);
        return super.onCreateOptionsMenu(menu);
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
        }
        return false;
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

    private final View.OnLongClickListener detLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(MCAccidents.currentPoint.id);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

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

    public void parseSendMessageResponse(JSONObject json, int currentId) {
        if (json.has("result")) {
            try {
                String result = json.getString("result");
                if (result.equals("OK")) {
                    Toast.makeText(this, Startup.context.getString(R.string.send_success), Toast.LENGTH_LONG).show();
                    MCAccidents.refresh(Startup.context);
                    update();
                    detailMessagesFragment.notifyDataSetChanged();
                    //mcNewMessageText.setText("");
                    //Keyboard.hide(findViewById(R.id.mc_new_message_text));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Send message failed", json.toString());
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.send_error), Toast.LENGTH_LONG).show();
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
        intent.putExtra("toMap", MCAccidents.currentPoint.id);
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
        return prefs;
    }
}
