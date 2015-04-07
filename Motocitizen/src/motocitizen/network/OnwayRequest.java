package motocitizen.network;

import org.json.JSONObject;

import motocitizen.Activity.AccidentDetails;
import motocitizen.app.mc.MCListeners;
import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 03.04.15.
 */
public class OnwayRequest extends HttpClient {

    private AccidentDetails activity;
    private int currentId;

    public OnwayRequest(AccidentDetails activity, int currentId) {
        super(activity, activity.getString(R.string.request_onway));
        this.currentId = currentId;
        this.activity = activity;
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        activity.parseOnwayResponse(result, currentId);
    }
}
