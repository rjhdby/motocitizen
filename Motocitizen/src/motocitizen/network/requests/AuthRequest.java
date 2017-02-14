package motocitizen.network.requests;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class AuthRequest extends HTTPClient {

    public AuthRequest(String login, String password) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        post.put("calledMethod", Methods.AUTH.toCode());
        post.put("versionName", String.valueOf(Preferences.getInstance().getAppVersion()));
        post.put("login", login);
        post.put("passwordHash", User.getInstance().getPassHash(password));
    }

    public JSONObject execute() {
        return request(post);
    }

    @Override
    public boolean error(JSONObject response) {
        return !response.has("id");
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("id")) return "Ошибка соединения " + response.toString();
        try {
            return response.getString("id").equals("0") ? "Ошибка авторизации" : "Успешная авторизация";
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
