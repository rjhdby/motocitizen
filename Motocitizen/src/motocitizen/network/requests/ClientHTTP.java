package motocitizen.network.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
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

import motocitizen.MyApp;
import motocitizen.network.CustomTrustManager;


public class ClientHTTP extends AsyncTask<Map<String, String>, Integer, JSONObject> {
    private final static String CHARSET = "UTF-8";
    private final static String USERAGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    ProgressDialog dialog;
    Context context;
    private MyApp myApp;

    @Override
    protected JSONObject doInBackground(Map<String, String>... params) {
        JSONObject result;
        URL url;
        String app;
        Map<String, String> request = params[0];
        try {
            if(request.containsKey("app")) {
                app = request.get("app");
                request.remove("app");
            } else{
                app = myApp.getProps().get("default.app");
            }
            String method = request.get("method");
            request.remove("method");
            url = createUrl(app, method, false);
            if (request.containsKey("hint")) {
                final String hint = request.get("hint");
                request.remove("hint");
                Runnable execute = new Runnable() {
                    @Override
                    public void run() {
                        dialog = new ProgressDialog(context);
                        dialog.setMessage("Обмен данными...\n" + hint);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                };
                ((Activity) context).runOnUiThread(execute);
            }
            return request(url, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public JSONObject request(URL url, Map<String, String> post) {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        CustomTrustManager.allowAllSSL();
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            connection.setRequestProperty("Content-Language", "ru-RU");
            connection.setRequestProperty("User-Agent", USERAGENT);
            connection.setUseCaches(false);

            if (!post.isEmpty()) {
                String POST = makePOST(post);
                Log.d("POST", url.toString() + "?" + POST);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString((POST).getBytes().length));
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(POST);
            }
            InputStream is;
            try {
                is = connection.getInputStream();
                if (connection.getContentEncoding() != null) {
                    is = new GZIPInputStream(is);
                }
                int responseCode = connection.getResponseCode();
                Log.d("JSON ERROR", String.valueOf(responseCode));
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } else {
                    Log.d("JSON ERROR", String.valueOf(responseCode));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        JSONObject reader;
        try {
            reader = new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            reader = new JSONObject();
        }
        Log.d("JSON RESPONSE", reader.toString());
        return reader;
    }

    private String makePOST(Map<String, String> post) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (final String key : post.keySet())
            try {
                if (first)
                    first = false;
                else
                    result.append("&");
                if (post.get(key) == null) {
                    //TODO Caused by: java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
                    /*
                    Runnable execute = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Не задано " + key, Toast.LENGTH_LONG).show();
                        }
                    };
                    ((Activity) context).runOnUiThread(execute);
*/
                    return "ERROR";
                }
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(post.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        // Log.d("JSON POST", result.toString());
        return result.toString();
    }
    public URL createUrl(String app, String method, Boolean https) {
        String protocol;
        if (https) {
            protocol = "https";
        } else {
            protocol = "http";
        }
        String script;
        String defaultMethod = myApp.getProps().get("app." + app + ".json.method.default");
        String server = myApp.getProps().get("app." + app + ".json.server");
        if (myApp.getProps().containsKey("app." + app + ".json.method." + method)) {
            script = myApp.getProps().get("app." + app + ".json.method." + method);
        } else {
            script = defaultMethod;
        }
        try {
            return new URL(protocol + "://" + server + "/" + script);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void dismiss() {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            dialog = null;
        }
    }
}