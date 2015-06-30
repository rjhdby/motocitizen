package motocitizen.app.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import motocitizen.MyApp;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.MyPreferences;

//import motocitizen.startup.Startup;

@SuppressLint("UseSparseArrays")
public class Accidents {
    /*
    private static final int NORMAL = R.drawable.accident_row_gradient;
    private static final int HIDE   = R.drawable.accident_row_gradient_hide;
    private static final int ENDED  = R.drawable.accident_row_gradient_ended;
    */
    private static final int NORMAL = 0xff808080;
    private static final int HIDE   = 0xff202020;
    private static final int ENDED  = 0xff606060;
    public final  String                 error;
    private       Map<Integer, Accident> points;
    private final MyPreferences          prefs;
    private final Context                context;

    public enum Sort {
        FORWARD, BACKWARD
    }

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
        new AccidentsRequest(context, new AccidentsRequestCallback());
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

    static int getBackground(String status) {
        if (status.equals("acc_status_end")) {
            return ENDED;
        }
        if (status.equals("acc_status_hide")) {
            return HIDE;
        }
        return NORMAL;
    }

    public String toString(int id) {
        return points.get(id).getTextToCopy();
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {

            if (result.has("error")) {
                try {
                    Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Неизвестная ошибка" + result.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                try {
                    parseJSON(result.getJSONArray("list"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Map<Integer, Accident> getVisibleAccidents() {
        Map<Integer, Accident> out = new HashMap<>();
        for (int i : points.keySet()) {
            Accident point = points.get(i);
            if (point.isInvisible()) continue;
            if (point.getHoursAgo() >= prefs.getHoursAgo()) continue;
            out.put(i, point);
        }
        return out;
    }

    public Integer[] sort(Map<Integer, Accident> in, Sort FLAG) {
        List<Integer> list = new ArrayList<>();
        list.addAll(in.keySet());
        Integer[] out = new Integer[list.size()];
        switch (FLAG) {
            case FORWARD:
                list.toArray(out);
                Arrays.sort(out);
                break;
            case BACKWARD:
                list.toArray(out);
                Arrays.sort(out, Collections.reverseOrder());
                break;
            default:
                list.toArray(out);
        }
        return out;
    }
}
