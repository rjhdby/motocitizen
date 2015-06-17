package motocitizen.network.requests;

import android.content.Context;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GeocodeRequest extends HTTPClient {
    public GeocodeRequest(AsyncTaskCompleteListener listener, Location location, Context context) {
        this.context = context;
        this.listener = listener;
        post = new HashMap<>();
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lon", String.valueOf(location.getLongitude()));
        post.put("method", "geocode");
        //post.put("hint", "Геокодирование");
        this.execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        try {
            response.getString("address");
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public String getError(JSONObject response) {
        return "Неизвестная ошибка" + response.toString();
    }
}
