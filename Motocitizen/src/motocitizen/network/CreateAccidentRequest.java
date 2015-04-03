package motocitizen.network;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.Activity.CreateAccActivity;
import motocitizen.app.mc.MCLocation;

/**
 * Created by elagin on 03.04.15.
 */
public class CreateAccidentRequest extends HttpClient  {

    private CreateAccActivity activity;

    public CreateAccidentRequest(CreateAccActivity activity) {
        this.activity = activity;
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        activity.parseResponse(result);
    }
}
