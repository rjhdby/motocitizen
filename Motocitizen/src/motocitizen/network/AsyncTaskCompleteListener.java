package motocitizen.network;

import org.json.JSONException;
import org.json.JSONObject;

public interface AsyncTaskCompleteListener {
    void onTaskComplete(JSONObject result) throws JSONException;
}
