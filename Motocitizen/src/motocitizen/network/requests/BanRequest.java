package motocitizen.network.requests;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class BanRequest extends HTTPClient {
    public BanRequest(Context context, AsyncTaskCompleteListener listener, int id) {
        this.listener = listener;
        this.context = context;
        int user_id = AccidentsGeneral.points.getPoint(id).getOwnerId();
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("id", String.valueOf(id));
        post.put("user_id", String.valueOf(user_id));
        post.put("calledMethod", "ban");
        execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        if (!response.has("result")) return true;
        try {
            String result = response.getString("ban");
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
            String result = response.getString("ban");
            switch (result) {
                case "OK":
                    return "Статус изменен";
                case "ERROR PREREQUISITES":
                    return "Неизвестная ошибка " + response.toString();
                case "NO USER":
                    return "Пользователь не зарегистрирован";
                case "AUTH ERROR":
                case "NO RIGHTS":
                    return "Недостаточно прав";
            }
        } catch (JSONException e) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}