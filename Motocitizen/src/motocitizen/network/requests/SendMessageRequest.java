package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class SendMessageRequest extends HTTPClient {
    public SendMessageRequest(AsyncTaskCompleteListener listener, int id, String text) {
        this.listener = listener;
        post.put("login", User.dirtyRead().getName());
        post.put("passhash", User.dirtyRead().getPassHash());
        post.put("id", String.valueOf(id));
        post.put("text", text);
        post.put("calledMethod", Methods.MESSAGE.toCode());
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
                    return "Сообщение отправлено";
                case "ERROR":
                    return "Вы не авторизованы";
                case "READONLY":
                    return "Недостаточно прав";
            }
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
