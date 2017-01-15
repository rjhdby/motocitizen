package motocitizen.network.requests;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.Auth;
import motocitizen.utils.Preferences;

public class AuthRequest extends HTTPClient {
    public AuthRequest() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        post = new HashMap<>();
        post.put("calledMethod", Methods.AUTH.toCode());
        post.put("versionName", String.valueOf(Preferences.getInstance().getAppVersion()));
    }

    public void setLogin(String login) {
        post.put("login", login);
    }

    public void setPassword(String password) {
        post.put("passwordHash", Auth.makePassHash(password));
    }

    public JSONObject execute() {
        return super.request(post);
    }

    @Override
    public boolean error(JSONObject response) {
        return !response.has("id");
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("id")) return "Ошибка соединения " + response.toString();
        try {
            String result = response.getString("id");
            if (result.equals("0")) {
                return "Ошибка авторизации";
            } else {
                return "Успешная авторизация";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
