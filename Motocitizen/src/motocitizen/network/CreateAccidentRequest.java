package motocitizen.network;

import org.json.JSONObject;

import motocitizen.Activity.CreateAccActivity;
import motocitizen.main.R;

/**
 * Created by elagin on 03.04.15.
 */
public class CreateAccidentRequest extends HttpClient  {

    private final CreateAccActivity activity;

    public CreateAccidentRequest(CreateAccActivity activity) {
        super(activity, activity.getString(R.string.request_create_acc));
        this.activity = activity;
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();
        activity.parseResponse(result);
    }
}
