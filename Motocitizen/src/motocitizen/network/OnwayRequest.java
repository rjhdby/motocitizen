package motocitizen.network;

import org.json.JSONObject;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.main.R;

/**
 * Created by elagin on 03.04.15.
 */
public class OnwayRequest extends HttpClient {

    private final AccidentDetailsActivity activity;
    private int currentId;

    public OnwayRequest(AccidentDetailsActivity activity, int currentId) {
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
