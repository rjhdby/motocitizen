package motocitizen.dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

import motocitizen.content.accident.Accident;
import motocitizen.content.accident.AccidentFactory;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.AccidentsRequest;

public class Content extends TreeMap<Integer, Accident> {
    private int inPlace = 0;

    private Content(Comparator<Integer> comparator) {
        super(comparator);
    }

    private static class Holder {
        private static Content instance;
    }

    public static Content getInstance() {
        if (Holder.instance == null) {
            Holder.instance = new Content(Collections.reverseOrder());
        }
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
        JSONArray list = new JSONArray();
        try {
            list = json.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.length(); i++) {
            try {
                Accident accident = AccidentFactory.Companion.make(list.getJSONObject(i));
                put(accident.getId(), accident);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLeave(int currentInplace) {
        //TODO SetLeave
    }
}
