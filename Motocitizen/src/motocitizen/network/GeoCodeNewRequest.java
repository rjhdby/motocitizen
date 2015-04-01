package motocitizen.network;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.mc.MCCreateAcc;
import motocitizen.app.mc.MCLocation;

/**
 * Created by elagin on 01.04.15.
 */
public class GeoCodeNewRequest extends HttpClient {

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();

        try {
            MCCreateAcc.addressText = result.getString("address");
            MCCreateAcc.writeGlobal();
        } catch (JSONException e) {
            MCLocation.address = "Ошибка геокодирования";
            MCCreateAcc.writeGlobal();
            e.printStackTrace();
        }
    }
}
