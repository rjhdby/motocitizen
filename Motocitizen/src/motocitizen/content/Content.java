package motocitizen.content;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.database.Favorites;
import motocitizen.draw.Rows;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.utils.Sort;

public class Content {
    private Map<Integer, Accident> points;
    private int                    inPlace;
    public  List<Integer>          favorites;

    {
        points = new HashMap<>();
        inPlace = 0;
    }

    public Content() {
        favorites = Favorites.getFavorites();
    }

    public Accident getPoint(int id) {
        return points.get(id);
    }

    public int getInplaceId() {
        return inPlace;
    }

    public void setInPlace(int id) {
        if (inPlace != 0) {
            //TODO setLeave
        }
        inPlace = id;
    }

    public void update(AsyncTaskCompleteListener listener) {
        new AccidentsRequest(listener, true);
    }

    private FrameLayout noAccidentsNotification() {
        FrameLayout fl = new FrameLayout(MyApp.getCurrentActivity());
        TextView    tv = new TextView(MyApp.getCurrentActivity());
        tv.setText("Нет событий");
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.GRAY);
        fl.setBackgroundColor(Color.GRAY);
        fl.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return fl;
    }

    public void refresh() {
        update();
        MyApp.getMap().placeAccidents();
        redraw();
    }

    public void update() {
        new AccidentsRequest(new AccidentsRequestCallback(), true);
    }

    public void redraw() {
        ViewGroup view = (ViewGroup) MyApp.getCurrentActivity().findViewById(R.id.accListContent);

        if (view == null) return;
        view.removeAllViews();

        //TODO YesterdayRow ???
        //TODO Нет событий

        for (int id : Sort.getSortedAccidentsKeys(points)) {
            if (points.get(id).isInvisible()) continue;
            view.addView(Rows.getAccidentRow(view, points.get(id)));
        }
    }

    public void parseJSON(JSONObject json) {
        if (!json.has("list")) return;
        try {
            JSONArray list = json.getJSONArray("list");
            Log.d("START PARSE POINTS", String.valueOf((new Date()).getTime()));
            for (int i = 0; i < list.length(); i++) {
                Accident accident = new Accident(list.getJSONObject(i));
                if (accident.isError()) continue;
                if (points.containsKey(accident.getId())) {
                    points.get(accident.getId()).update(list.getJSONObject(i));
                } else {
                    points.put(accident.getId(), accident);
                }
            }
            Log.d("END PARSE POINTS", String.valueOf((new Date()).getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLeave(int currentInplace) {
        //TODO SetLeave
    }

    public Map<Integer, Accident> getPoints() {
        return points;
    }

    public Accident get(int id) {
        return points.get(id);
    }

    public Set<Integer> getIds() {
        return points.keySet();
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {

        public void onTaskComplete(JSONObject result) {
            if (!result.has("error")) parseJSON(result);
            MyApp.getContent().redraw();
            MyApp.getMap().placeAccidents();
        }
    }
}
