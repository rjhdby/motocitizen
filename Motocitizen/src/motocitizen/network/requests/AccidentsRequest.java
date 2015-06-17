package motocitizen.network.requests;

import android.content.Context;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.MyApp;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.MyLocationManager;
import motocitizen.startup.MyPreferences;

public class AccidentsRequest extends HTTPClient {
    public AccidentsRequest(AsyncTaskCompleteListener listener, Context context) {
        this.context = context;
        this.listener = listener;
        post = new HashMap<>();
        Location location = MyLocationManager.getLocation(context);
        myApp = (MyApp) context.getApplicationContext();
        MyPreferences prefs = new MyPreferences(context);
        String user = prefs.getLogin();
        if (!user.equals("")) {
            post.put("user", user);
        }
        if (AccidentsGeneral.points.keySet().size() != 0) {
            post.put("update", "1");
        }
        post.put("distance", String.valueOf(prefs.getVisibleDistance()));
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        //post.put("hint", context.getString(R.string.request_get_incidents));
        post.put("calledMethod", "getlist");
        this.execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        if (!response.has("list")) return true;
        try {
            JSONObject error = response.getJSONArray("list").getJSONObject(0);
            if (error.has("error")) return true;
        } catch (JSONException e) {
            return true;
        }
        return false;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("list")) return "Неизвестная ошибка " + response.toString();
        try {
            JSONObject json = response.getJSONArray("list").getJSONObject(0);
            if (json.has("error")) {
                String error = json.getString("error");
                if (error.equals("no_new")) {
                    return "Нет новых сообщений";
                }
            } else {
                return "Список обновлен";
            }
        } catch (JSONException e) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
