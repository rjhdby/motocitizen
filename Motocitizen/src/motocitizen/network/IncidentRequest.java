package motocitizen.network;

import org.json.JSONObject;

import motocitizen.app.mc.MCAccidents;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 01.04.15.
 */
public class IncidentRequest extends HttpClient  {

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();
        MCAccidents.refreshPoints(Startup.context, result);
    }
}
