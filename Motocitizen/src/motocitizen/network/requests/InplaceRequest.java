package motocitizen.network.requests;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import motocitizen.app.general.AccidentsGeneral;

public class InplaceRequest extends HTTPClient {
    public InplaceRequest (Context context, int id){
        this.context = context;
        post = new HashMap<>();
        post.put("login", AccidentsGeneral.auth.getLogin());
        post.put("id", String.valueOf(id));
        post.put("calledMethod", "inplace");
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
                    return "Статус изменен успешно";
                case "ERROR PREREQUISITES":
                    return "Неизвестная ошибка" + response.toString();
            }
        } catch (JSONException e) {

        }
        return "Неизвестная ошибка" + response.toString();
    }
}
