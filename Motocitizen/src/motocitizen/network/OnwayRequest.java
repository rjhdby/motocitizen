package motocitizen.network;

import org.json.JSONObject;

import motocitizen.app.mc.MCListeners;
import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 03.04.15.
 */
public class OnwayRequest extends HttpClient {

    private int currentId;

    public OnwayRequest(int currentId) {
        super(Startup.context.getString(R.string.request_onway));
        this.currentId = currentId;
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        MCListeners.parseOnwayResponse(result, currentId);
    }
}
