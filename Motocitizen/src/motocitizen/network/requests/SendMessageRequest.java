package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class SendMessageRequest extends HTTPClient {
    @SuppressWarnings("unchecked")
    public SendMessageRequest(AsyncTaskCompleteListener listener, int id, String text) {
        this.listener = listener;
        post.put("login", Preferences.getInstance().getLogin());
        post.put("passhash", User.getInstance().makePassHash());
        post.put("id", String.valueOf(id));
        post.put("text", text);
        post.put("calledMethod", Methods.MESSAGE.toCode());
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
            String result = response.getString("result");
            switch (result) {
                case "OK":
                    return "Сообщение отправлено";
                case "ERROR":
                    return "Вы не авторизованы";
                case "READONLY":
                    return "Недостаточно прав";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
