package motocitizen.network.requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import motocitizen.network.CustomTrustManager;
import motocitizen.startup.Startup;


abstract class HTTPClient extends AsyncTask<Map<String, String>, Integer, JSONObject> {
    /* constants */
    private final static String CHARSET       = "UTF-8";
    private final static String USERAGENT     = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    private final static String SERVER        = "forum.moto.msk.ru/mobile/main_mc_acc_json.php";
    private final static int    GOOD_RESPONSE = 200;
    /* end constants */

    private   ProgressDialog            dialog;
    protected AsyncTaskCompleteListener listener;
    protected Map<String, String>       post;

    @SafeVarargs
    @Override
    protected final JSONObject doInBackground(Map<String, String>... params) {
        return request(params[0]);
    }

    JSONObject request(Map<String, String> post) {
        if (!Startup.isOnline()) {
            try {
                JSONObject result = new JSONObject();
                result.put("error", "Интернет не доступен");
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }
        URL url;
        try {
            url = createUrl(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new JSONObject();
        }
            /*
            if (post.containsKey("hint")) {
                final String hint = post.get("hint");
                post.remove("hint");
                Runnable execute = new Runnable() {
                    @Override
                    public void run() {
                        dialog = new ProgressDialog(MyApp.getCurrentActivity());
                        dialog.setMessage("Обмен данными...\n" + hint);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                };
                ((Activity) context).runOnUiThread(MyApp.getCurrentActivity());
            }
            */

        StringBuilder response = new StringBuilder();
        CustomTrustManager.allowAllSSL();
        HttpURLConnection connection = null;
        try {
            connection = getConnection(url);
            if (!post.isEmpty()) {
                String POST = makePOST(post);
                Log.d("POST", url.toString() + "?" + POST);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString((POST).getBytes().length));
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(POST);
            }
            InputStream is;
            is = connection.getInputStream();
            if (connection.getContentEncoding() != null) {
                is = new GZIPInputStream(is);
            }
            int responseCode = connection.getResponseCode();
            Log.d("JSON ERROR", String.valueOf(responseCode));
            if (responseCode == GOOD_RESPONSE) {
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                Log.d("JSON ERROR", String.valueOf(responseCode));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        JSONObject reader;
        //TODO порнография какая то
        try {
            reader = new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                reader = new JSONObject(response.toString().replace("\\", "").replace("\"", ""));
            } catch (JSONException e1) {
                e1.printStackTrace();
                String fakeAnswer = "{ error : unknown }";
                try {
                    reader = new JSONObject(fakeAnswer);
                } catch (JSONException e2) {
                    //Абсолютно маловероятно
                    e2.printStackTrace();
                    reader = new JSONObject();
                }
            }
        }
        Log.d("JSON RESPONSE", reader.toString());
        return reader;
    }

    private HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
        connection.setRequestProperty("Content-Language", "ru-RU");
        connection.setRequestProperty("User-Agent", USERAGENT);
        connection.setUseCaches(false);
        return connection;
    }

    private String makePOST(Map<String, String> post) {
        StringBuilder result = new StringBuilder();
        //TODO Нафига здесь final?
        for (final String key : post.keySet())
            try {
                result.append("&");
                if (post.get(key) == null) {
                    //TODO Caused by: java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
                    return "ERROR";
                }
                result.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(post.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return result.toString().substring(1);
    }

    private URL createUrl(Boolean https) throws MalformedURLException {
        String protocol;
        protocol = https ? "https" : "http";
        return new URL(protocol + "://" + SERVER);
    }

    private void dismiss() {
        try {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            dialog = null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (error(result) && !result.has("error")) {
            String error = getError(result);
            result = new JSONObject();
            try {
                result.put("error", error);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
