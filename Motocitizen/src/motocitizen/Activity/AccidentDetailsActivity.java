package motocitizen.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.app.mc.MCPointHistory;
import motocitizen.app.mc.MCVolunteer;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.network.OnwayRequest;
import motocitizen.network.SendMessageRequest;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

import static motocitizen.app.mc.MCAccidents.getDelimiterRow;

public class AccidentDetailsActivity extends ActionBarActivity implements View.OnClickListener {

    private int id;
    private MCPoint currentPoint;

    private Button newMessageButton;
    private EditText mcNewMessageText;
    private Button onwayButton;

    private RadioGroup mcDetTabsGroup;
    private View detMessages;
    private View detHistory;
    private View detVolunteers;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_details);

        newMessageButton = (Button) findViewById(R.id.mc_new_message_send);
        newMessageButton.setOnClickListener(this);

        mcNewMessageText = (EditText) findViewById(R.id.mc_new_message_text);
        mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);

        onwayButton = (Button) findViewById(R.id.onway_button);
        onwayButton.setOnClickListener(this);

        mcDetMessagesTable = findViewById(R.id.mc_det_messages_table);
        onwayContent = findViewById(R.id.acc_onway_table);
        inplaceContent = findViewById(R.id.acc_inplace_table);
        mcDetLogContent = findViewById(R.id.mc_det_log_content);

        /*
        * Описание группы закладок внутри деталей происшествия
        */
        mcDetTabsGroup = (RadioGroup) findViewById(R.id.mc_det_tabs_group);
        mcDetTabsGroup.setOnCheckedChangeListener(accDetTabsListener);

        detMessages = findViewById(R.id.det_messages);
        detHistory = findViewById(R.id.det_history);
        detVolunteers = findViewById(R.id.det_volunteers);

        generalType = (TextView) findViewById(R.id.acc_details_general_type);
        generalStatus = (TextView) findViewById(R.id.acc_details_general_status);
        generalTime = (TextView) findViewById(R.id.acc_details_general_time);
        generalOwner = (TextView) findViewById(R.id.acc_details_general_owner);
        generalAddress = (TextView) findViewById(R.id.acc_details_general_address);
        generalDescription = (TextView) findViewById(R.id.acc_details_general_description);
        ((ScrollView) findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);

        update();
    }

    private void update() {

        currentPoint = MCAccidents.points.getPoint(MCAccidents.currentPoint.id);

        generalType.setText(currentPoint.getTypeText() + ". " + currentPoint.getMedText());
        generalStatus.setText(currentPoint.getStatusText());
        generalTime.setText(Const.timeFormat.format(currentPoint.created.getTime()));
        generalOwner.setText(currentPoint.owner);
        generalAddress.setText("(" + currentPoint.getDistanceText() + ") " + currentPoint.address);
        generalDescription.setText(currentPoint.descr);

        if (currentPoint.id == MCAccidents.getOnwayID() || currentPoint.id == MCAccidents.getInplaceID()) {
            onwayButton.setVisibility(View.INVISIBLE);
        } else {
            onwayButton.setVisibility(View.VISIBLE);
        }

        /*
         * Выводим список сообщений
		 */
        findViewById(R.id.mc_acc_details_general).setOnLongClickListener(detLongClick);
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        for (int i : currentPoint.getSortedMessagesKeys()) {
            messageView.addView(currentPoint.messages.get(i).createRow(this));
        }

		/*
         * Выводим список волонтеров
		 */
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

		/*
		 * Выводим историю
		 */
        ViewGroup logView = (ViewGroup) mcDetLogContent;
        logView.removeAllViews();
        logView.addView(MCPointHistory.createHeader(this));
        for (int i : currentPoint.getSortedHistoryKeys()) {
            logView.addView(currentPoint.history.get(i).createRow(this));
        }
        Startup.map.zoom(16);
        Startup.map.jumpToPoint(currentPoint.location);

        setupAccess();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accident_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            Intent intent = new Intent(this, Startup.class);
            intent.putExtra("toMap", MCAccidents.currentPoint.id);
            this.startActivity(intent);
            return true;
        } else if (id == R.id.action_newMessage) {
            Intent intent = new Intent(this, MessagesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("messageID", MCAccidents.currentPoint.id);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAccess() {
        View newMessageArea = findViewById(R.id.mc_new_message_area);

        if (MCRole.isStandart()) {
            newMessageArea.setVisibility(View.VISIBLE);
        } else {
            newMessageArea.setVisibility(View.INVISIBLE);
        }
    }

    private final View.OnLongClickListener detLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(MCAccidents.currentPoint.id);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    private final TextWatcher mcNewMessageTextListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String temp = s.toString().replaceAll("\\s", "");
            if (temp.length() == 0) {
                newMessageButton.setEnabled(false);
            } else {
                newMessageButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public final RadioGroup.OnCheckedChangeListener accDetTabsListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            detMessages.setVisibility(View.INVISIBLE);
            detHistory.setVisibility(View.INVISIBLE);
            detVolunteers.setVisibility(View.INVISIBLE);
            if (id == R.id.mc_det_tab_messages) {
                detMessages.setVisibility(View.VISIBLE);
            } else if (id == R.id.mc_det_tab_history) {
                detHistory.setVisibility(View.VISIBLE);
            } else if (id == R.id.mc_det_tab_people) {
                detVolunteers.setVisibility(View.VISIBLE);
            }
        }
    };

    public void parseSendMessageResponse(JSONObject json, int currentId) {
        if (json.has("result")) {
            try {
                String result = json.getString("result");
                if (result.equals("OK")) {
                    Toast.makeText(this, Startup.context.getString(R.string.send_succsess), Toast.LENGTH_LONG).show();
                    MCAccidents.refresh(Startup.context);
                    update();
                    mcNewMessageText.setText("");
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
                    Toast.makeText(this, Startup.context.getString(R.string.send_succsess), Toast.LENGTH_LONG).show();
                    MCAccidents.setOnwayID(currentId);
                    MCAccidents.refresh(Startup.context);
                    update();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.mc_new_message_send:
                OnNewMessageSendButton();
                break;
            case R.id.onway_button:
                OnWayButton();
                break;
            default:
                Log.e("AccidentDetailsActivity", "Unknow button pressed");
                break;
        }
    }

    public void OnNewMessageSendButton() {
        if (Startup.isOnline()) {
            String text = mcNewMessageText.getText().toString();
            int currentId = MCAccidents.currentPoint.id;
            Map<String, String> post = new HashMap<>();
            post.put("login", MCAccidents.auth.getLogin());
            post.put("passhash", MCAccidents.auth.makePassHash());
            post.put("id", String.valueOf(currentId));
            post.put("text", text);
            JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
            if (request != null) {
                (new SendMessageRequest(this, currentId)).execute(request);
            }
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
    }

    public void OnWayButton() {
        if (Startup.isOnline()) {
            int currentId = MCAccidents.currentPoint.id;
            Map<String, String> post = new HashMap<>();
            post.put("login", MCAccidents.auth.getLogin());
            post.put("passhash", MCAccidents.auth.makePassHash());
            post.put("id", String.valueOf(currentId));
            JsonRequest request = new JsonRequest("mcaccidents", "onway", post, "", true);
            if (request != null) {
                (new OnwayRequest(this, currentId)).execute(request);
            }
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
    }
}
