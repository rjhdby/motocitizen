package motocitizen.network.requests;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.geolocation.MyLocationManager;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.utils.Preferences;

public class AccidentsRequest extends HTTPClient {
    private boolean silent;

    @SuppressWarnings("unchecked")
    public AccidentsRequest(AsyncTaskCompleteListener listener, boolean silent) {
        this.silent = silent;
        this.listener = listener;
        Location location = MyLocationManager.getInstance().getLocation();
        String   user     = Preferences.getInstance().getLogin();
        if (!user.equals("")) {
            post.put("user", user);
        }
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        post.put("age", String.valueOf(Preferences.getInstance().getHoursAgo()));
        post.put("m", Methods.GET_LIST.toCode());
        execute(post);
    }

    public AccidentsRequest(AsyncTaskCompleteListener listener) {
        new AccidentsRequest(listener, false);
    }

    @Override
    public boolean error(JSONObject response) {
        if (silent) return false;
        try {
            if (!response.getJSONArray("list").getJSONObject(0).has("error")) return false;
        } catch (JSONException | NullPointerException ignored) {}
        return true;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("list")) return "Ошибка соединения " + response.toString();
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
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
