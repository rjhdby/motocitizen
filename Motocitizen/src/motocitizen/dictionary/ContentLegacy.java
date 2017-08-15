package motocitizen.dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

import motocitizen.content.accident.Accident;
import motocitizen.content.accident.AccidentFactory;
import motocitizen.network.ApiRequest;
import motocitizen.network.requests.AccidentListRequest;

public class ContentLegacy extends TreeMap<Integer, Accident> {
    private int inPlace = 0;

    private ContentLegacy(Comparator<Integer> comparator) {
        super(comparator);
    }

    private static class Holder {
        private static ContentLegacy instance;
    }

    public static ContentLegacy getInstance() {
        if (Holder.instance == null) {
            Holder.instance = new ContentLegacy(Collections.reverseOrder());
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

    public void requestUpdate(ApiRequest.RequestResultCallback listener) {
        new AccidentListRequest(listener);
    }

    public void requestUpdate() {
        new AccidentListRequest(result -> {if (!result.has("error")) parseJSON(result);});
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
                Accident accident = AccidentFactory.INSTANCE.make(list.getJSONObject(i));
                put(accident.getId(), accident);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получаем json о всех событиях, но обновляем экран, только с открытым событием
     *
     * @param json
     * @param ID
     */
    public void parseJSON(JSONObject json, int ID) {
        JSONArray list = new JSONArray();
        try {
            list = json.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.length(); i++) {
            try {
                JSONObject ooObject = list.getJSONObject(i);
                if (ooObject.getInt("id") == ID) {
                    Accident accident = AccidentFactory.INSTANCE.make(list.getJSONObject(i));
                    put(accident.getId(), accident);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLeave(int currentInplace) {
        //TODO SetLeave
    }
}
