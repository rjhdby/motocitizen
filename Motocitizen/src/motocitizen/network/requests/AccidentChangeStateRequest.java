package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.MyApp;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;

public class AccidentChangeStateRequest extends HTTPClient {

    @SuppressWarnings("unchecked")
    public AccidentChangeStateRequest(AsyncTaskCompleteListener listener, int id, String state) {
        this.listener = listener;
        post = new HashMap<>();
        post.put("login", MyApp.getAuth().getLogin());
        post.put("passhash", MyApp.getAuth().makePassHash());
        post.put("state", state);
        post.put("id", String.valueOf(id));
        post.put("m", Methods.CHANGE_STATE.toCode());
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
                    return "Статус изменен успешно";
                case "ERROR":
                    return "Вы не авторизированы";
                case "NO RIGHTS":
                case "READONLY":
                    return "Недостаточно прав";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
