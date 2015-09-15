package motocitizen.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.app.general.user.Auth;
import motocitizen.database.Favorites;
import motocitizen.draw.Rows;
import motocitizen.draw.Sort;
import motocitizen.gcm.GCMRegistration;
import motocitizen.geolocation.MyLocationManager;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.Startup;

public class Content {
    public static Auth auth;
    private static boolean                noError     = true;
    private static Map<Integer, Accident> points      = new HashMap<>();
    private static int                    inPlace     = 0;
    private static boolean                initialized = false;
    public static List<Integer> favorites;

    public Content(Context context) {
        MyApp myApp = (MyApp) context.getApplicationContext();
        auth = myApp.getMCAuth();
        new MyLocationManager(context);
        new GCMRegistration(context);
        favorites = Favorites.getFavorites(context);
        initialized = true;
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

    public static void update(Context context, AsyncTaskCompleteListener listener) {
        new AccidentsRequest(context, listener, true);
    }

    private static FrameLayout noAccidentsNotification(Context context) {
        FrameLayout fl = new FrameLayout(context);
        TextView    tv = new TextView(context);
        tv.setText("Нет событий");
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.GRAY);
        fl.setBackgroundColor(Color.GRAY);
        fl.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return fl;
    }

    public static void refresh(Context context) {
        update(context);
        Startup.map.placeAccidents(context);
        redraw(context);
    }

    public static void update(Context context) {
        new AccidentsRequest(context, new AccidentsRequestCallback(context), true);
    }

    public static void update(Context context, AccidentsRequestCallback listener) {
        new AccidentsRequest(context, listener, true);
    }

    public static void redraw(Context context) {
        ViewGroup view = (ViewGroup) ((Activity) context).findViewById(R.id.accListContent);

        if (view == null) return;
        view.removeAllViews();

        //TODO YesterdayRow ???
        //TODO Нет событий

        for (int id : Sort.getSortedAccidentsKeys(points)) {
            if (points.get(id).isInvisible()) continue;
            view.addView(Rows.getAccidentRow(context, view, points.get(id)));
        }
    }

    public static void refreshPoints(Context context) {
        update(context);
        redraw(context);
        Startup.map.placeAccidents(context);
        //points.saveReadMessages();
    }

    public static void toDetails(Context context, int id) {
        Intent intent = new Intent(context, AccidentDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("accidentID", id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void parseJSON(Context context, JSONObject json) {
        noError = true;
        if (json.has("list")) {
            try {
                JSONArray list = json.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    Accident accident = new Accident(context, list.getJSONObject(i));
                    if (accident.isNoError()) {
                        if (points.containsKey(accident.getId())) {
                            points.get(accident.getId()).update(list.getJSONObject(i));
                        } else {
                            points.put(accident.getId(), accident);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                noError = false;
            }
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
        private Context context;

        public AccidentsRequestCallback(final Context context) {
            this.context = context;
        }

        public void onTaskComplete(JSONObject result) {
            if (!result.has("error")) parseJSON(context, result);
            Content.redraw(context);
            Startup.map.placeAccidents(context);
        }
    }
}
