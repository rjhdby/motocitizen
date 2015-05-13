package motocitizen.network;

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

public class HttpClient extends AsyncTask<JsonRequest, Void, JSONObject> {

    ProgressDialog dialog;
    private final String info;
    private final Context context;
    private final boolean isCreateDialog;
    private MyApp myApp = null;

    public HttpClient(Context context, String info) {
        this.info = info;
        this.context = context;
        this.isCreateDialog = true;
        myApp = (MyApp) context.getApplicationContext();
    }

    public HttpClient(Context context, String info, boolean isCreateDialog) {
        this.info = info;
        this.context = context;
        this.isCreateDialog = isCreateDialog;
        myApp = (MyApp) context.getApplicationContext();
    }

    private final static String CHARSET = "UTF-8";
    private final static String USERAGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    private URL url;
    private String method = null;

    protected void onPreExecute() {
        if (isCreateDialog && !info.equals("")) {
            Runnable execute = new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(context);
                    dialog.setMessage("Обмен данными...\n" + info);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(true);
                    dialog.show();
                }
            };
            ((Activity) context).runOnUiThread(execute);
        }
    }

    @Override
    protected JSONObject doInBackground(JsonRequest... params) {
        JSONObject result = null;
        if (params.length > 0) {
            JsonRequest item = params[0];
            createUrl(item.app, item.method, false);
            result = request(item.params);
        }
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    public void createUrl(String app, String method, Boolean https) {
        String protocol;
        if (https) {
            protocol = "https";
        } else {
            protocol = "http";
        }
        this.method = method;
        String script;
        String defaultMethod = myApp.getProps().get("app." + app + ".json.method.default");
        String server = myApp.getProps().get("app." + app + ".json.server");
        if (myApp.getProps().containsKey("app." + app + ".json.method." + method)) {
            script = myApp.getProps().get("app." + app + ".json.method." + method);
        } else {
            script = defaultMethod;
        }
        try {
            url = new URL(protocol + "://" + server + "/" + script);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public JSONObject request(Map<String, String> post) {
        post.put("calledMethod", method);
        // Log.d("JSON CALL", "|" + url.toString() + "|");
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        CustomTrustManager.allowAllSSL();
        try {
            // URL url = new URL(server);
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
//                is = new GZIPInputStream(is);
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

    protected void dismiss() {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            dialog = null;
        }
    }
}
