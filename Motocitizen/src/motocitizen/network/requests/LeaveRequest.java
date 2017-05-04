package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class LeaveRequest extends HTTPClient {
    public LeaveRequest(int id) {
        post.put("login", User.dirtyRead().getName());
        post.put("id", String.valueOf(id));
        post.put("m", Methods.LEAVE.toCode());
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
