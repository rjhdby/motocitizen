package motocitizen.content;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.database.Favorites;
import motocitizen.draw.Rows;
import motocitizen.utils.Sort;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.Activity.MainScreenActivity;

public class Content {
    private static Map<Integer, Accident> points;
    private static int                    inPlace;
    private static boolean                initialized;
    public static  List<Integer>          favorites;

    static {
        initialized = true;
    }

    {
        points = new HashMap<>();
        inPlace = 0;
        initialized = false;
    }

    public Content() {
        favorites = Favorites.getFavorites();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static motocitizen.accident.Accident getPoint(int id) {
        return points.get(id);
    }

    public static int getInplaceID() {
        return getInPlace();
    }

    public static int getInPlace() {
        return inPlace;
    }

    public static void setInPlace(int id) {
        if (inPlace != 0) {
            //TODO setLeave
        }
        Content.inPlace = id;
    }

    public static void update(AsyncTaskCompleteListener listener) {
        new AccidentsRequest(listener, true);
    }

    private static FrameLayout noAccidentsNotification() {
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

    public static void refresh() {
        update();
        MyApp.getMap().placeAccidents();
        redraw();
    }

    public static void update() {
        new AccidentsRequest(new AccidentsRequestCallback(), true);
    }

    public static void redraw() {
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

    public static void refreshPoints() {
        update();
        redraw();
        MyApp.getMap().placeAccidents();
        //points.saveReadMessages();
    }

    public static void toDetails(int id) {
        Intent intent = new Intent(MyApp.getAppContext(), AccidentDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("accidentID", id);
        intent.putExtras(bundle);
        MyApp.getCurrentActivity().startActivity(intent);
    }

    public static void parseJSON(JSONObject json) {
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

    public static void setLeave(int currentInplace) {
        //TODO Setleave
    }

    public static Map<Integer, Accident> getPoints() {
        return points;
    }

    public static Accident get(int id) {
        return points.get(id);
    }

    public static Set<Integer> getIds() {
        return points.keySet();
    }

    private static class AccidentsRequestCallback implements AsyncTaskCompleteListener {

        public void onTaskComplete(JSONObject result) {
            if (!result.has("error")) parseJSON(result);
            Content.redraw();
            MyApp.getMap().placeAccidents();
        }
    }
}
