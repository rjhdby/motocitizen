package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.MyApp;
import motocitizen.content.Content;

public class CancelOnWayRequest extends HTTPClient {
    public CancelOnWayRequest(AsyncTaskCompleteListener listener, int id) {
        this.listener = listener;
        post = new HashMap<>();
        post.put("login", MyApp.getAuth().getLogin());
        post.put("passhash", MyApp.getAuth().makePassHash());
        post.put("id", String.valueOf(id));
        post.put("calledMethod", Methods.CANCEL_ONWAY.toCode());
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
                    return "Неизвестная ошибка " + response.toString();
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
