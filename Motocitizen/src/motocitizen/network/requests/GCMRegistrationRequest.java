package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class GCMRegistrationRequest extends HTTPClient {
    public GCMRegistrationRequest(String regId) {
        post.put("owner_id", String.valueOf(User.getInstance().getId()));
        post.put("gcm_key", regId);
        post.put("login", Preferences.getInstance().getLogin());
        //post.put("imei", imei);
        post.put("passhash", User.getInstance().getPassHash());
        post.put("calledMethod", Methods.REGISTER_GCM.toCode());
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
            if (response.getString("result").equals("OK")) return "Регистрация в GCM успешна";
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
