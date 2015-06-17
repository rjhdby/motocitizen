package motocitizen.network.requests;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class GCMRegistrationRequest extends HTTPClient {
    public GCMRegistrationRequest(Context context, String regId) {
        this.context = context;
        post = new HashMap<>();
        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        post.put("owner_id", String.valueOf(AccidentsGeneral.auth.getID()));
        post.put("gcm_key", regId);
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("imei", imei);
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("calledMethod", "registerGCM");
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
        if (!response.has("result")) return "Ошибка соединения с сервером"  + response.toString();
        try {
            String result = response.getString("result");
            switch (result) {
                case "OK":
                    return "Регистрация в GCM успешна";
                case "ERROR PREREQUISITES":
                    return "Не удалось зарегистрироваться в GCM " + response.toString();
            }
        } catch (JSONException e) {

        }
        return "Не удалось зарегистрироваться в GCM " + response.toString();
    }
}
