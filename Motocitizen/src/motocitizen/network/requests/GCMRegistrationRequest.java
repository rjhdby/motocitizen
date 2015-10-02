package motocitizen.network.requests;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.MyApp;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;

public class GCMRegistrationRequest extends HTTPClient {
    public GCMRegistrationRequest(String regId) {
        post = new HashMap<>();
        String imei = ((TelephonyManager) MyApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        post.put("owner_id", String.valueOf(MyApp.getAuth().getid()));
        post.put("gcm_key", regId);
        post.put("login", MyApp.getAuth().getLogin());
        post.put("imei", imei);
        post.put("passhash", MyApp.getAuth().makePassHash());
        post.put("calledMethod", Methods.REGISTER_GCM.toCode());
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
        if (!response.has("result")) return "Ошибка соединения "  + response.toString();
        try {
            String result = response.getString("result");
            switch (result) {
                case "OK":
                    return "Регистрация в GCM успешна";
                case "ERROR PREREQUISITES":
                    return "Ошибка соединения " + response.toString();
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
