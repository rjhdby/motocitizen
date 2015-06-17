package motocitizen.network.requests;

import org.json.JSONException;
import org.json.JSONObject;

public interface AsyncTaskCompleteListener {
    public void onTaskComplete(JSONObject result) throws JSONException;
}
