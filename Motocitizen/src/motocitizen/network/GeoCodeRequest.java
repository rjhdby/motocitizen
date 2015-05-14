package motocitizen.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.general.MyLocationManager;
import motocitizen.startup.Startup;

public class GeoCodeRequest extends HttpClient {


    public GeoCodeRequest (Context context) {
        //super(context, context.getString(R.string.request_geocode));
        super(context, "");
    }
    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        super.dismiss();

        try {
            MyLocationManager.address = result.getString("address");
            Startup.updateStatusBar(MyLocationManager.address);
        } catch (JSONException e) {
            MyLocationManager.address = "Ошибка геокодирования";
            Startup.updateStatusBar(MyLocationManager.address);
            e.printStackTrace();
        }
    }
}
