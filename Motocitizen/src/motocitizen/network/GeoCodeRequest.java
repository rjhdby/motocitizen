package motocitizen.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.mc.MCLocation;
import motocitizen.main.R;
import motocitizen.startup.Startup;

public class GeoCodeRequest extends HttpClient {


    public GeoCodeRequest (Context context) {
        //super(context, context.getString(R.string.request_geocode));
        super(context, "");
    }
    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if(dialog != null) {
            dialog.dismiss();
        }

        try {
            MCLocation.address = result.getString("address");
            Startup.updateStatusBar(MCLocation.address);
        } catch (JSONException e) {
            MCLocation.address = "Ошибка геокодирования";
            Startup.updateStatusBar(MCLocation.address);
            e.printStackTrace();
        }
    }
}
