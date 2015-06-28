package motocitizen.network.requests;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class AccidentChangeStateRequest extends HTTPClient {
    public static final String ACTIVE = "acc_status_act";
    public static final String ENDED  = "acc_status_end";
    public static final String HIDE   = "acc_status_hide";
    private final int    id;
    private final String state;

    public AccidentChangeStateRequest(AsyncTaskCompleteListener listener, Context context, int id, String state) {
        this.listener = listener;
        this.context = context;
        this.id = id;
        this.state = state;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("passhash", AccidentsGeneral.auth.makePassHash());
        post.put("state", state);
        post.put("id", String.valueOf(id));
        post.put("calledMethod", "changeState");
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
