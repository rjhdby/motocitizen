package motocitizen.network;

import org.json.JSONObject;

import motocitizen.app.mc.MCListeners;
import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 03.04.15.
 */
public class SendMessageRequest extends HttpClient {

    private int currentId;

    public SendMessageRequest(int currentId) {
        super(Startup.context.getString(R.string.request_send_message));
        this.currentId = currentId;
    }


    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        MCListeners.parseSendMessageResponse(currentId);
    }
}
