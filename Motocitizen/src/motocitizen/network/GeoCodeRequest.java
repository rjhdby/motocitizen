package motocitizen.network;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.mc.MCLocation;
import motocitizen.main.R;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 01.04.15.
 */
public class GeoCodeRequest extends HttpClient {


    public GeoCodeRequest () {
        super(Startup.context.getString(R.string.request_geocode));
    }
    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        try {
            MCLocation.address = result.getString("address");
            MCLocation.updateStatusBar();
        } catch (JSONException e) {
            MCLocation.address = "Ошибка геокодирования";
            MCLocation.updateStatusBar();
            e.printStackTrace();
        }
    }
}
