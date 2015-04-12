package motocitizen.network;

import android.content.Context;

import org.json.JSONObject;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.startup.Startup;

public class IncidentRequest extends HttpClient  {

    public IncidentRequest(Context context) {
        super(context, context.getString(R.string.request_get_incidents));
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();
        MCAccidents.refreshPoints(Startup.context, result);
    }
}
