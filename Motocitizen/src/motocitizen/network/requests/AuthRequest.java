package motocitizen.network.requests;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.user.Auth;

public class AuthRequest extends HTTPClient {
    public AuthRequest(Context context) {
        this.context = context;
        String ident = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        post = new HashMap<>();
        post.put("ident", ident);
        post.put("calledMethod", "auth");
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
