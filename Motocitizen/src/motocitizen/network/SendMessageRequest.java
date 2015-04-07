package motocitizen.network;

import org.json.JSONObject;

import motocitizen.Activity.AccidentDetails;
import motocitizen.main.R;

/**
 * Created by elagin on 03.04.15.
 */
public class SendMessageRequest extends HttpClient {

    private AccidentDetails activity;
    private int currentId;

    public SendMessageRequest(AccidentDetails activity, int currentId) {
        super(activity, activity.getString(R.string.request_send_message));
        this.currentId = currentId;
        this.activity = activity;
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        activity.parseSendMessageResponse(result, currentId);
    }
}
