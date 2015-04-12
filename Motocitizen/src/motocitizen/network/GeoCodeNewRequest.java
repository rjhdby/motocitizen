package motocitizen.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.Activity.CreateAccActivity;
import motocitizen.main.R;

public class GeoCodeNewRequest extends HttpClient {

    public GeoCodeNewRequest(Context context) {
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
            CreateAccActivity.updateAddress(result.getString("address"));
        } catch (JSONException e) {
            CreateAccActivity.updateAddress("Ошибка геокодирования");
            e.printStackTrace();
        }
    }
}
