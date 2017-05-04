package motocitizen.network.requests;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.geolocation.MyLocationManager;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class AccidentsRequest extends HTTPClient {
    private boolean silent;

    public AccidentsRequest(AsyncTaskCompleteListener listener, boolean silent) {
        this.silent = silent;
        this.listener = listener;
        Location location = MyLocationManager.getLocation();
        String   user     = User.dirtyRead().getName();
        if (!user.equals("")) {
            post.put("user", user);
        }
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        //noinspection ConstantConditions
        post.put("age", String.valueOf(Preferences.dirtyRead() == null ? 24 : Preferences.dirtyRead().getHoursAgo()));
        post.put("m", Methods.GET_LIST.toCode());
        //noinspection unchecked
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
            return json.has("error") && json.getString("error").equals("no_new") ? "Нет новых сообщений" : "Список обновлен";
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
