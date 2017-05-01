package motocitizen.dictionary;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.content.AccidentFactory;
import motocitizen.content.accident.Accident;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.utils.SortedHashMap;

public class Content extends SortedHashMap<Accident> {
    private int inPlace = 0;

    private Content() {
    }

    private static class Holder {
        private static Content instance;
    }

    public static void init() {
        Holder.instance = new Content();
    }

    public static Content getInstance() {
        return Holder.instance;
    }

    public int getInPlaceId() {
        return inPlace;
    }

    public void setInPlace(int id) {
        if (inPlace != 0) {
            //TODO setLeave
        }
        inPlace = id;
    }

    public void requestUpdate(AsyncTaskCompleteListener listener) {
        new AccidentsRequest(listener, true);
    }

    public void requestUpdate() {
        new AccidentsRequest(result -> {if (!result.has("error")) parseJSON(result);}, true);
    }

    public void parseJSON(JSONObject json) {
        if (!json.has("list")) return;
        try {
            JSONArray list = json.getJSONArray("list");
            Log.d("START PARSE POINTS", String.valueOf((new Date()).getTime()));
            for (int i = 0; i < list.length(); i++) {
                try {
                    Accident accident = AccidentFactory.Companion.make(list.getJSONObject(i));
                    put(accident.getId(), accident);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            Log.d("END PARSE POINTS", String.valueOf((new Date()).getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLeave(int currentInplace) {
        //TODO SetLeave
    }
}
