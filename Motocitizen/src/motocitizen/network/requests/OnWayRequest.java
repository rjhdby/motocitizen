package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class OnWayRequest extends HTTPClient {
    @SuppressWarnings("unchecked")
    public OnWayRequest(AsyncTaskCompleteListener listener, int id) {
        this.listener = listener;
        post.put("login", Preferences.getInstance().getLogin());
        post.put("passhash", User.getInstance().makePassHash());
        post.put("id", String.valueOf(id));
        post.put("m", Methods.ON_WAY.toCode());
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
            String result = response.getString("result");
            switch (result) {
                case "OK":
                    return "Статус изменен";
                case "ERROR PREREQUISITES":
                    return "Неизвестная ошибка " + response.toString();
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
