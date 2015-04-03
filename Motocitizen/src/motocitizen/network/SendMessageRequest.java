package motocitizen.network;

import org.json.JSONObject;

import motocitizen.app.mc.MCListeners;

/**
 * Created by elagin on 03.04.15.
 */
public class SendMessageRequest extends HttpClient {

    private int currentId;

    public SendMessageRequest(int currentId) {
        this.currentId = currentId;
    }


    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        MCListeners.parseSendMessageResponse(currentId);
    }
}
