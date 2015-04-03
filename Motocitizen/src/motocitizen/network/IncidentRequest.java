package motocitizen.network;

import org.json.JSONObject;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 01.04.15.
 */
public class IncidentRequest extends HttpClient  {

    public IncidentRequest() {
        super(Startup.context.getString(R.string.request_get_incidents));
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();
        MCAccidents.refreshPoints(Startup.context, result);
    }
}
