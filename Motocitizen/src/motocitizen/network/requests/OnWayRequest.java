package motocitizen.network.requests;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.content.Content;

public class OnWayRequest extends HTTPClient {
    public OnWayRequest(AsyncTaskCompleteListener listener, Context context, int id) {
        this.listener = listener;
        this.context = context;
        post = new HashMap<>();
        post.put("login", Content.auth.getLogin());
        post.put("passhash", Content.auth.makePassHash());
        post.put("id", String.valueOf(id));
        post.put("calledMethod", "onway");
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
                    return "Статус изменен";
                case "ERROR PREREQUISITES":
                    return "Неизвестная ошибка " + response.toString();
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
