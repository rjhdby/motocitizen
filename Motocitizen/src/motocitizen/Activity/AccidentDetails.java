package motocitizen.Activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCListeners;
import motocitizen.app.mc.MCObjects;
import motocitizen.app.mc.MCPoint;
import motocitizen.app.mc.MCPointHistory;
import motocitizen.app.mc.MCVolunteer;
import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.app.mc.user.MCRole;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.network.OnwayRequest;
import motocitizen.network.SendMessageRequest;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Text;

import static motocitizen.app.mc.MCAccidents.getDelimiterRow;

public class AccidentDetails extends ActionBarActivity {

    private int id;
    private MCPoint currentPoint;

    private Button newMessageButton;
    private EditText mcNewMessageText;

    private Button onwayButton;

    private RadioGroup mcDetTabsGroup;
    private View detMessages;
    private View detHistory;
    private View detVolunteers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_details);

        // makeDetails
        currentPoint = MCAccidents.points.getPoint(MCAccidents.currentPoint.id);

        newMessageButton = (Button) findViewById(R.id.mc_new_message_send);
        newMessageButton.setOnClickListener(newMessageButtonListener);

        mcNewMessageText = (EditText)findViewById(R.id.mc_new_message_text);
        mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);

        onwayButton = (Button) findViewById(R.id.onway_button);
        onwayButton.setOnClickListener(onwayButtonListener);

            /*
     * Описание группы закладок внутри деталей происшествия
     */
        mcDetTabsGroup = (RadioGroup) findViewById(R.id.mc_det_tabs_group);
        mcDetTabsGroup.setOnCheckedChangeListener(accDetTabsListener);

        detMessages = findViewById(R.id.det_messages);
        detHistory = findViewById(R.id.det_history);
        detVolunteers = findViewById(R.id.det_volunteers);

        TextView generalType = (TextView) findViewById(R.id.acc_details_general_type);
        generalType.setText(currentPoint.getTypeText() + ". " + currentPoint.getMedText());

        TextView generalStatus = (TextView) findViewById(R.id.acc_details_general_status);
        generalStatus.setText(currentPoint.getStatusText());

        TextView generalTime = (TextView) findViewById(R.id.acc_details_general_time);
        generalTime.setText(Const.timeFormat.format(currentPoint.created.getTime()));

        TextView generalOwner = (TextView) findViewById(R.id.acc_details_general_owner);
        generalOwner.setText(currentPoint.owner);

        TextView generalAddress = (TextView) findViewById(R.id.acc_details_general_address);
        generalAddress.setText("(" + currentPoint.getDistanceText() + ") " + currentPoint.address);

        TextView generalDescription = (TextView) findViewById(R.id.acc_details_general_description);
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
        ((ScrollView)findViewById(R.id.mc_det_messages_scroll)).fullScroll(View.FOCUS_UP);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupAccess() {
        View newMessageArea = findViewById(R.id.mc_new_message_area);

        if (MCRole.isStandart()) {
            newMessageArea.setVisibility(View.VISIBLE);
        } else {
            newMessageArea.setVisibility(View.INVISIBLE);
        }
    }

    private static final View.OnLongClickListener detLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(MCAccidents.currentPoint.id);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    public static final Button.OnClickListener newMessageButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            if (Startup.isOnline()) {
                String text = Text.get(R.id.mc_new_message_text);
                int currentId = MCAccidents.currentPoint.id;
                Map<String, String> post = new HashMap<>();
                post.put("login", MCAccidents.auth.getLogin());
                post.put("passhash", MCAccidents.auth.makePassHash());
                post.put("id", String.valueOf(currentId));
                post.put("text", text);
                JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
                if (request != null) {
                    (new SendMessageRequest(currentId)).execute(request);
                }
            } else {
                Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
            }
        }
    };

    public static final Button.OnClickListener onwayButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Startup.isOnline()) {
                int currentId = MCAccidents.currentPoint.id;
                Map<String, String> post = new HashMap<>();
                post.put("login", MCAccidents.auth.getLogin());
                post.put("passhash", MCAccidents.auth.makePassHash());
                post.put("id", String.valueOf(currentId));
                JsonRequest request = new JsonRequest("mcaccidents", "onway", post, "", true);
                if (request != null) {
                    (new OnwayRequest(currentId)).execute(request);
                }
            } else {
                Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
            }
        }
    };

    public final TextWatcher mcNewMessageTextListener = new TextWatcher() {

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
}
