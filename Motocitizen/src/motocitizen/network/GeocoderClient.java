package motocitizen.network;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GeocoderClient {
    private static final String       url    = "https://maps.googleapis.com/maps/api/geocode/json?language=ru&address=";
    private static final OkHttpClient client = new OkHttpClient();

    public static LatLng latLngByAddress(String address) throws IOException, JSONException {
        address = url + address.replace(' ', '+');
        Request request = new Request.Builder()
                .url(address)
                .build();
        Response   response = client.newCall(request).execute();
        JSONObject json     = new JSONObject(response.body().string());
        JSONArray  results  = json.getJSONArray("results");
        if (results.length() == 0) throw new IOException();
        JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

//        Log.e("ADDRESS", response.body().string());
        return new LatLng(location.getDouble("lat"), location.getDouble("lng"));
    }
}
