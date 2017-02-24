package motocitizen.network;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


abstract public class HTTPClient extends AsyncTask<Map<String, String>, Integer, JSONObject> {
    private static final String       url    = "http://motodtp.info/mobile/main_mc_acc_json.php";
    private final        OkHttpClient client = new OkHttpClient();

    private   ProgressDialog            dialog;
    protected AsyncTaskCompleteListener listener;
    protected final Map<String, String> post     = new HashMap<>();
    protected       JSONObject          response = new JSONObject();
//    protected JSONObject          error    = new JSONObject();

    private final Map<String, String> fake = new HashMap<>(1);

    @SafeVarargs
    @Override
    protected final JSONObject doInBackground(Map<String, String>... params) {
//        return request(params[ 0 ]);
        return request();
    }

    private JSONObject request() {
        return request(post);
    }

    protected JSONObject request(Map<String, String> post) {
        Request request = new Request.Builder()
                .post(makePost(post))
                .url(url)
                .build();
        try {
            String text = client.newCall(request)
                                .execute()
                                .body()
                                .string();
            Log.w("HTTP RESPONSE", text);
            response = new JSONObject(text);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            fake.put("error", "unknown");
            response = new JSONObject(fake);
        }
        return response;
    }

    private static RequestBody makePost(Map<String, String> post) {
        StringBuilder debug = new StringBuilder();
        debug.append(url).append("?");
        FormBody.Builder body = new FormBody.Builder();
        for (String key : post.keySet()) {
            body.add(key, post.get(key));
            debug.append(key).append("=").append(post.get(key)).append("&");
        }
        Log.w("HTTP REQUEST", debug.toString());
        return body.build();
    }

    private void dismiss() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        dialog = null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        dismiss();
        if (listener == null) return;

        if (error(result) && !result.has("error")) {
            fake.put("error", getError(result));
            result = new JSONObject(fake);
        }
        listener.onTaskComplete(result);
    }

    protected abstract boolean error(JSONObject response);

    protected abstract String getError(JSONObject response);
}
