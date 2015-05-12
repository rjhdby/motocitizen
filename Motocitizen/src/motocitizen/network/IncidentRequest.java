package motocitizen.network;

import android.content.Context;

import org.json.JSONObject;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.startup.Startup;

public class IncidentRequest extends HttpClient {

    private final Startup activity;

    public IncidentRequest(Startup activity, boolean isCreateDialog) {
        super(activity, activity.getString(R.string.request_get_incidents), isCreateDialog);
        this.activity = activity;
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        super.dismiss();
        activity.resetUpdating();
        MCAccidents.refreshPoints(Startup.context, result);
    }
}
