package motocitizen.network.requests;

import android.content.Context;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import motocitizen.content.Content;
import motocitizen.content.Medicine;
import motocitizen.content.Type;
import motocitizen.main.R;
import motocitizen.utils.Const;

public class CreateAccidentRequest extends HTTPClient {
    public CreateAccidentRequest(AsyncTaskCompleteListener listener, Context context) {
        this.context = context;
        this.listener = listener;
        post = new HashMap<>();
        post.put("status", "acc_status_act");
        post.put("calledMethod", Methods.CREATE.toCode());
        post.put("hint", context.getString(R.string.request_create_acc));
        post.put("owner_id", String.valueOf(Content.auth.getid()));
        post.put("login", Content.auth.getLogin());
        post.put("passhash", Content.auth.makePassHash());
    }

    public void execute() {
        super.execute(post);
    }

    public void setLocation(Location location) {
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
    }

    public void setType(Type type) {
        post.put("type", type.toCode());
    }

    public void setAddress(String address) {
        post.put("address", address);
    }

    public void setDescription(String description) {
        post.put("descr", description);
    }

    public void setCreated(Date created) {
        post.put("created", Const.dateFormat.format(created));
    }

    public void setMed(Medicine medicine) {
        post.put("med", medicine.toCode());
    }

    public void setStatus(String status) {
        post.put("status", status);
    }

    @Override
    public boolean error(JSONObject response) {
        if (!response.has("result")) return true;
        try {
            JSONObject result = response.getJSONObject("result");
            if (result.has("ID")) return false;
        } catch (JSONException e) {
            return true;
        }
        return true;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("result")) return "Ошибка соединения " + response.toString();
        try {
            if (response.getJSONObject("result").has("ID")) return "Сообщение отправлено";
            String result = response.getString("result");
            switch (result) {
                case "AUTH ERROR":
                    return "Вы не авторизованы";
                case "NO RIGHTS":
                case "READONLY":
                    return "Недостаточно прав";
                case "PROBABLY SPAM":
                    return "Нельзя создавать события так часто";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
