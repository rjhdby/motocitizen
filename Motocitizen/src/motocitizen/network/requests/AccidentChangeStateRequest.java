package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;

public class AccidentChangeStateRequest extends HTTPClient {

    public AccidentChangeStateRequest(AsyncTaskCompleteListener listener, int id, String state) {
        this.listener = listener;
        post.put("login", User.dirtyRead().getName());
        post.put("passhash", User.dirtyRead().getPassHash());
        post.put("state", state);
        post.put("id", String.valueOf(id));
        post.put("m", Methods.CHANGE_STATE.toCode());
        //noinspection unchecked
        execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        try {
            if (response.getString("result").equals("OK")) return false;
        } catch (JSONException | NullPointerException ignored) {}
        return true;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("result")) return "Ошибка соединения " + response.toString();
        try {
            switch (response.getString("result")) {
                case "OK":
                    return "Статус изменен успешно";
                case "ERROR":
                    return "Вы не авторизированы";
                case "NO RIGHTS":
                case "READONLY":
                    return "Недостаточно прав";
            }
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
