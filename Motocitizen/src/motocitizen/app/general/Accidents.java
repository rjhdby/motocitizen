package motocitizen.app.general;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.MyPreferences;
import motocitizen.utils.Const;

//import motocitizen.startup.Startup;

@SuppressLint("UseSparseArrays")
public class Accidents {
    private static final int NORMAL = R.drawable.accident_row_gradient;
    private static final int HIDE = R.drawable.accident_row_gradient_hide;
    private static final int ENDED = R.drawable.accident_row_gradient_ended;
    public final String error;
    private Map<Integer, Accident> points;
    private MyPreferences prefs;
    private Context context;

    public Accidents(Context context) {
        error = "ok";
        if (points == null) {
            points = new HashMap<>();
        }
        this.context = context;
        prefs = ((MyApp) context.getApplicationContext()).getPreferences();
    }

    public boolean containsKey(int id) {
        return points.containsKey(id);
    }

    public Accident getPoint(int id) {
        return points.get(id);
    }

    public Set<Integer> keySet() {
        return points.keySet();
    }

    public void load() {
        new AccidentsRequest(new AccidentsRequestCallback(), context);
     }


    public void update(JSONArray data) {
        try {
            parseJSON(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(JSONArray json) throws JSONException {
        if (((JSONObject) json.get(0)).has("error")) return;
        for (int i = 0; i < json.length(); i++) {
            JSONObject acc = json.getJSONObject(i);
            try {
                Accident current = new Accident(acc, context);
                if (points.containsKey(current.getId())) {
                    current.messages.putAll(points.get(current.getId()).messages);
                }
                addPoint(current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addPoint(Accident point) {
        points.put(point.getId(), point);
    }

    int getFirstNonNull() {
        for (int i : points.keySet()) {
            if (points.get(i).row_id != 0) {
                return i;
            }
        }
        return 0;
    }

    public void setSelected(Context context, int id) {
        for (int i : points.keySet()) {
            Accident p = points.get(i);
            if (p.row_id == 0) {
                continue;
            }
            View row = ((Activity) context).findViewById(p.row_id);
            row.setBackgroundResource(getBackground(p.getStatusString()));
        }
        Accident selected = points.get(id);
        if (selected == null) {
            return;
        }
        if (selected.row_id == 0) {
            int nnid = getFirstNonNull();
            if (nnid == 0) {
                //noinspection UnnecessaryReturnStatement
                return;
            } else {
                setSelected(context, nnid);
                AccidentsGeneral.setCurrentPoint(points.get(nnid));
                AccidentsGeneral.redraw(context);
            }
        } else {
            View row = ((Activity) context).findViewById(selected.row_id);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) (4 * Const.getDP(context));
            lp.setMargins(margin, 0, margin, 0);
            row.setLayoutParams(lp);
            row.setPadding(0, margin, 0, margin);

            selected.resetMessagesUnreadFlag();
        }
    }

    int getBackground(String status) {
        if (status.equals("acc_status_end")) {
            return ENDED;
        }
        if (status.equals("acc_status_hide")) {
            return HIDE;
        }
        return NORMAL;
    }

    public String getTextToCopy(int id) {
        return points.get(id).getTextToCopy();
    }
    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            try {
                parseJSON(result.getJSONArray("list"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
