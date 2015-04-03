package motocitizen.network;

import org.json.JSONObject;

import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 03.04.15.
 */
public class registerGCMRequest extends HttpClient {
    public registerGCMRequest() {
        super(Startup.context.getString(R.string.request_google_gcm));
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();
    }
}
