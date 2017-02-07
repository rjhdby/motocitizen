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
import okhttp3.Response;


abstract public class HTTPClient extends AsyncTask<Map<String, String>, Integer, JSONObject> {
    private static final String       url    = "http://motodtp.info/mobile/main_mc_acc_json.php";
    private final        OkHttpClient client = new OkHttpClient();

    private   ProgressDialog            dialog;
    protected AsyncTaskCompleteListener listener;
    protected Map<String, String> post = new HashMap<>();

    @SafeVarargs
    @Override
    protected final JSONObject doInBackground(Map<String, String>... params) {
        return request(params[ 0 ]);
    }

    protected JSONObject request(Map<String, String> post) {
        Request request = new Request.Builder().post(makePost(post)).url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String   text     = response.body().string();
            Log.d("HTTP RESPONSE", text);
            return new JSONObject(text);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Map<String, String> fake = new HashMap<>();
            fake.put("error", "unknown");
            return new JSONObject(fake);
        }
    }

    private static RequestBody makePost(Map<String, String> post) {
        FormBody.Builder body = new FormBody.Builder();
        for (String key : post.keySet()) {
            body.add(key, post.get(key));
        }
        return body.build();
    }

    private void dismiss() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        dialog = null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (error(result) && !result.has("error")) {
            Map<String, String> content = new HashMap<>(1);
            content.put("error", getError(result));
            result = new JSONObject(content);
        }
        if (listener != null) {
            try {
                listener.onTaskComplete(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dismiss();
    }

    protected abstract boolean error(JSONObject response);

    protected abstract String getError(JSONObject response);
}
