package motocitizen.network;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import motocitizen.app.mc.MCAccidents;
import motocitizen.startup.Startup;

/**
 * Created by elagin on 31.03.15.
 */
public class HttpClient extends AsyncTask<JsonRequest, Void, JSONArray> {

    public ProgressDialog dialog;

    protected void onPreExecute() {
        dialog = new ProgressDialog(Startup.context);
        dialog.setMessage("Обмен данными...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected JSONArray doInBackground(JsonRequest... params) {
        if( params.length > 0 ){
            JsonRequest item = params[0];
            JSONArray result = null;

            try {
                result = new JSONCall(item.app, item.method, false).request(item.params).getJSONArray(item.arrayName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            return null;
        }
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);
        dialog.dismiss();
        MCAccidents.refreshNew(Startup.context, result);
    }
}
