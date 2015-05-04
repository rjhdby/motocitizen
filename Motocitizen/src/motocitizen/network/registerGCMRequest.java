package motocitizen.network;

import android.content.Context;

import org.json.JSONObject;

import motocitizen.main.R;

/**
 * Created by elagin on 03.04.15.
 */
public class registerGCMRequest extends HttpClient {
    public registerGCMRequest(Context context, boolean isCreateDialog) {
        super(context, context.getString(R.string.request_google_gcm), isCreateDialog);
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        super.dismiss();
    }
}
