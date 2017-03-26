package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.utils.Preferences;

public class InPlaceRequest extends HTTPClient {
    public InPlaceRequest(int id) {
        post.put("login", Preferences.getInstance().getLogin());
        post.put("id", String.valueOf(id));
        post.put("m", Methods.IN_PLACE.toCode());
        //noinspection unchecked
        execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        try {
            if (response.getString("result").equals("OK")) return false;
        } catch (JSONException | NullPointerException ignored) {}
        return true;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("result")) return "Ошибка соединения " + response.toString();
        try {
            if (response.getString("result").equals("OK")) return "Статус изменен";
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
