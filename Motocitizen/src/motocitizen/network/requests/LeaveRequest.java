package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.utils.Preferences;

public class LeaveRequest extends HTTPClient {
    @SuppressWarnings("unchecked")
    public LeaveRequest(int id) {
        post = new HashMap<>();
        post.put("login", Preferences.getInstance().getLogin());
        post.put("id", String.valueOf(id));
        post.put("m", Methods.LEAVE.toCode());
        execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        if (!response.has("result")) return true;
        try {
            String result = response.getString("result");
            if (result.equals("OK")) return false;
        } catch (JSONException e) {
            return true;
        }
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
                    return "Неизвестная ошибка" + response.toString();
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
