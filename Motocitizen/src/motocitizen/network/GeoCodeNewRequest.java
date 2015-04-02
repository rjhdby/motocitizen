package motocitizen.network;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.Activity.CreateAccActivity;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 01.04.15.
 */
public class GeoCodeNewRequest extends HttpClient {

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        dialog.dismiss();
        try {
            CreateAccActivity.updateAddress(result.getString("address"));
        } catch (JSONException e) {
            CreateAccActivity.updateAddress("Ошибка геокодирования");
            e.printStackTrace();
        }
    }
}
