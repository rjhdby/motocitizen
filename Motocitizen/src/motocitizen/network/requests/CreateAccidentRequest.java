package motocitizen.network.requests;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.dictionary.Medicine;
import motocitizen.dictionary.Type;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.HTTPClient;
import motocitizen.network.Methods;
import motocitizen.user.User;
import motocitizen.utils.DateUtils;

public class CreateAccidentRequest extends HTTPClient {
    public CreateAccidentRequest(AsyncTaskCompleteListener listener) {
        this.listener = listener;
        post.put("status", "acc_status_act");
        post.put("calledMethod", Methods.CREATE.toCode());
        post.put("owner_id", String.valueOf(User.dirtyRead().getId()));
        post.put("login", User.dirtyRead().getName());
        post.put("passhash", User.dirtyRead().getPassHash());
    }

    public void setLocation(LatLng location) {
        post.put("lat", String.valueOf(location.latitude));
        post.put("lon", String.valueOf(location.longitude));
    }

    public void setForStat() {
        post.put("stat", "1");
    }

    public void setType(Type type) {
        post.put("type", type.code());
    }

    public void setAddress(String address) {
        post.put("address", address);
    }

    public void setDescription(String description) {
        post.put("descr", description);
    }

    public void setCreated(Date created) {
        post.put("created", DateUtils.getDbFormat(created));
    }

    public void setMed(Medicine medicine) {
        post.put("med", medicine.code());
    }

    @Override
    public boolean error(JSONObject response) {
        try {
            if (response.getJSONObject("result").has("ID")) return false;
        } catch (JSONException | NullPointerException ignored) {}
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
        } catch (JSONException ignored) {}
        return "Неизвестная ошибка " + response.toString();
    }
}
