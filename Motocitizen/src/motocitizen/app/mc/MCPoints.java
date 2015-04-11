package motocitizen.app.mc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.network.JsonRequest;
import motocitizen.startup.MCPreferences;
import motocitizen.utils.Const;

//import motocitizen.startup.Startup;

@SuppressLint("UseSparseArrays")
public class MCPoints {
    private static final int NORMAL = R.drawable.accident_row_gradient;
    private static final int HIDE = R.drawable.accident_row_gradient_hide;
    private static final int ENDED = R.drawable.accident_row_gradient_ended;
    public final String error;
    private Map<Integer, MCPoint> points;
    private MCPreferences prefs;
    private static Context context;

    public MCPoints(Context context) {
        error = "ok";
        if (points == null) {
            points = new HashMap<>();
        }
        this.context = context;
        prefs = new MCPreferences(context);
    }

    public boolean containsKey(int id) {
        return points.containsKey(id);
    }

    public MCPoint getPoint(int id) {
        return points.get(id);
    }

    public Set<Integer> keySet() {
        return points.keySet();
    }

    public void load() {
        Map<String, String> selector = new HashMap<>();
        Location userLocation = MCLocation.current;
        selector.put("distance", String.valueOf(prefs.getVisibleDistance()));
        selector.put("lon", String.valueOf(userLocation.getLongitude()));
        selector.put("lat", String.valueOf(userLocation.getLatitude()));
        String user = prefs.getLogin();
        if (!user.equals("")) {
            selector.put("user", user);
        }
        if (!points.isEmpty()) {
            selector.put("update", "1");
        }

        try {
            parseJSON(new JSONCall("mcaccidents", "getlist", false).request(selector).getJSONArray("list"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void update(JSONArray data) {
        try {
            parseJSON(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JsonRequest getLoadRequest() {
        Map<String, String> selector = new HashMap<>();
        Location userLocation = MCLocation.current;
        selector.put("distance", String.valueOf(prefs.getVisibleDistance()));
        selector.put("lon", String.valueOf(userLocation.getLongitude()));
        selector.put("lat", String.valueOf(userLocation.getLatitude()));
        String user = prefs.getLogin();
        if (!user.equals("")) {
            selector.put("user", user);
        }
        if (!points.isEmpty()) {
            selector.put("update", "1");
        }

        return new JsonRequest("mcaccidents", "getlist", selector, "list", false);
    }

    private void parseJSON(JSONArray json) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject acc = json.getJSONObject(i);
            try {
                MCPoint current = new MCPoint(acc, context);
                if (points.containsKey(current.id)) {
                    current.messages.putAll(points.get(current.id).messages);
                }
                points.put(current.id, current);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
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
            MCPoint p = points.get(i);
            if (p.row_id == 0) {
                continue;
            }
            View row = ((Activity) context).findViewById(p.row_id);
            row.setBackgroundResource(getBackground(p.status));
        }
        MCPoint selected = points.get(id);
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
                MCAccidents.currentPoint = points.get(nnid);
                MCAccidents.redraw(context);
            }
        } else {
            View row = ((Activity) context).findViewById(selected.row_id);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) (4 * Const.dp);
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
}
