package motocitizen.app.general;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.utils.MyUtils;

public class AccidentHistory {
    private static final Map<String, String> actions;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("create_mc_acc", "создал");
        m.put("onway", "выехал");
        m.put("cancel", "не выехал");
        m.put("inplace", "приехал");
        m.put("leave", "уехал");
        m.put("finish_mc_acc", "отбой");
        m.put("acc_status_hide", "скрыл");
        m.put("acc_status_act", "открыл");
        m.put("acc_status_end", "отбой");
        actions = Collections.unmodifiableMap(m);
    }

    public       int    id;
    public       int    owner_id;
    public       int    table_row;
    public final int    acc_id;
    public       String owner, action;
    public Date time;

    public AccidentHistory(JSONObject json, int acc_id) throws JSONException {
        this.acc_id = acc_id;
        id = json.getInt("id");
        owner_id = json.getInt("id_user");
        owner = json.getString("owner");
        action = json.getString("action");
        time = new Date(Long.parseLong(json.getString("uxtime"), 10) * 1000);
    }

    String getAction() {
        if (actions.containsKey(action)) {
            return actions.get(action);
        } else {
            return "другое";
        }
    }

    private void inflateHeader(Context context, ViewGroup tableLayout) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.history_row, tableLayout, false);
        ((TextView) tr.findViewById(R.id.owner)).setText("Кто");
        ((TextView) tr.findViewById(R.id.text)).setText("Что");
        ((TextView) tr.findViewById(R.id.date)).setText("Когда");
        tableLayout.addView(tr);
    }

    public void inflateRow(Context context, ViewGroup tableLayout) {
        if (tableLayout.getChildCount() == 0) {
            inflateHeader(context, tableLayout);
        }
        LayoutInflater li        = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr        = (TableRow) li.inflate(R.layout.history_row, tableLayout, false);
        TextView       ownerView = (TextView) tr.findViewById(R.id.owner);
        if (owner.equals(AccidentsGeneral.auth.getLogin())) {
            ownerView.setBackgroundColor(Color.DKGRAY);
        }
        ownerView.setText(owner);
        ((TextView) tr.findViewById(R.id.text)).setText(getAction());
        ((TextView) tr.findViewById(R.id.date)).setText(MyUtils.getStringTime(time, true));
        tableLayout.addView(tr);
    }
}
