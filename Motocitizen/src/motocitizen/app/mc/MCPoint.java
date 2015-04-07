package motocitizen.app.mc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.main.R;
import motocitizen.utils.Const;
import motocitizen.utils.MCUtils;
import motocitizen.utils.NewID;

@SuppressLint({"UseSparseArrays", "RtlHardcoded"})
public class MCPoint {
    public Map<String, String> attributes;
    public Map<Integer, MCMessage> messages;
    public Map<Integer, MCVolunteer> volunteers;
    public Map<Integer, MCPointHistory> history;
    public Location location;

    public String type, status, med, address, owner, descr;
    public Date created;

    public int id, owner_id, row_id;
    private final View.OnClickListener accRowClick = new View.OnClickListener() {
        public void onClick(View v) {
            MCAccidents.toDetails(v.getContext(), id);
        }
    };

    private final OnLongClickListener rowLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(id);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    public MCPoint() {
        id = 0;
    }

    public MCPoint(JSONObject json) throws MCPointException {
        Map<String, String> data = buildDataSet(json);
        if (!checkPrerequisites(data))
            throw new MCPointException();
        attributes = new HashMap<>();

        try {
            location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(Float.parseFloat(data.get("lat")));
            location.setLongitude(Float.parseFloat(data.get("lon")));
            location.setAccuracy(0);
            type = data.get("mc_accident_orig_type");
            status = data.get("status");
            med = data.get("mc_accident_orig_med");
            id = Integer.parseInt(data.get("id"));
            address = data.get("address");
            created = Const.dateFormat.parse(data.get("created"));
            owner = data.get("owner");
            owner_id = Integer.parseInt(data.get("owner_id"));
            descr = data.get("descr");

            data.remove("lat");
            data.remove("lon");
            data.remove("mc_accident_orig_type");
            data.remove("status");
            data.remove("mc_accident_orig_med");
            data.remove("id");
            data.remove("address");
            data.remove("created");
            data.remove("owner");
            data.remove("owner_id");

            for (String key : data.keySet()) {
                attributes.put(key, data.get(key));
            }
            makeMessages(json.getJSONArray("messages"));
            makeVolunteers(json.getJSONArray("onway"));
            makeHistory(json.getJSONArray("history"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new MCPointException();
        }
    }

    private Map<String, String> buildDataSet(JSONObject json) {
        Map<String, String> data = new HashMap<>();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equals("onway") || key.equals("messages")) {
                continue;
            }
            try {
                data.put(key, json.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public List<Integer> getSortedMessagesKeys() {
        List<Integer> keys = new ArrayList<>(messages.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);
        return keys;
    }

    public List<Integer> getSortedVolunteersKeys() {
        List<Integer> keys = new ArrayList<>(volunteers.keySet());
        Collections.sort(keys);
        return keys;
    }

    public List<Integer> getSortedHistoryKeys() {
        List<Integer> keys = new ArrayList<>(history.keySet());
        Collections.sort(keys);
        return keys;
    }

    boolean hasInOwners() {
        if (MCAccidents.auth.anonim)
            return false;
        int user = MCAccidents.auth.id;
        if (user == owner_id)
            return true;
        for (int key : messages.keySet()) {
            if (user == messages.get(key).owner_id)
                return true;
        }
        for (int key : volunteers.keySet()) {
            if (user == volunteers.get(key).id)
                return true;
        }
        for (int key : history.keySet()) {
            if (user == history.get(key).owner_id)
                return true;
        }
        return false;
    }

    private void makeMessages(JSONArray json) {
        if (messages == null)
            messages = new HashMap<>();
        Map<Integer, MCMessage> newMessages = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                MCMessage current = new MCMessage(json.getJSONObject(i), id);
                // current.acc_id = id;
                if (messages.containsKey(current.id)) {
                    current.unread = messages.get(current.id).unread;
                }
                newMessages.put(current.id, current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        messages.putAll(newMessages);
    }

    private int countUnreadMessages() {
        int counter = 0;
        for (int i : messages.keySet()) {
            if (messages.get(i).unread)
                counter++;
        }
        return counter;
    }

     public void resetMessagesUnreadFlag() {
        for (int i : messages.keySet()) {
            messages.get(i).unread = false;
        }
    }

    private void makeVolunteers(JSONArray json) {
        if (volunteers == null)
            volunteers = new HashMap<>();
        Map<Integer, MCVolunteer> newVolunteers = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                MCVolunteer current = new MCVolunteer(json.getJSONObject(i));
                newVolunteers.put(current.id, current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        volunteers.putAll(newVolunteers);
    }

    private void makeHistory(JSONArray json) {
        if (history == null)
            history = new HashMap<>();
        history.clear();
        for (int i = 0; i < json.length(); i++) {
            try {
                MCPointHistory current = new MCPointHistory(json.getJSONObject(i), id);
                history.put(current.id, current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public FrameLayout createAccRow(Context context) {
        ViewGroup vg = (ViewGroup) ((Activity) context).findViewById(R.id.accListContent);
        FrameLayout fl = (FrameLayout) Const.li.inflate(R.layout.accident_row, vg, false);
        TextView general = (TextView) fl.findViewById(R.id.accident_row_content);
        TextView time = (TextView) fl.findViewById(R.id.accident_row_time);
        TextView unreadView = (TextView) fl.findViewById(R.id.accident_row_unread);
        View iWasHere = fl.findViewById(R.id.i_was_here);
        row_id = NewID.id();
        fl.setId(row_id);
        fl.setOnLongClickListener(rowLongClick);
        fl.setOnClickListener(accRowClick);
        if (hasInOwners()) {
            iWasHere.setBackgroundColor(0xFFC62828);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeText());
        if (!med.equals("mc_m_na")) {
            sb.append(", ").append(getMedText());
        }
        sb.append("(").append(getDistanceText()).append(")\n").append(address).append("\n").append(descr);
        general.setText(sb);
        // time.setText(Const.timeFormat.format(created.getTime()));
        time.setText(MCUtils.getIntervalFromNowInText(created));
        int unread = countUnreadMessages();
        String msgText = "<b>" + String.valueOf(messages.size()) + "</b>";
        if (unread > 0) {
            msgText += "<font color=#C62828><b>(" + String.valueOf(countUnreadMessages()) + ")</b></font>";
        }
        unreadView.setText(Html.fromHtml(msgText));
        return fl;
    }

    private boolean checkPrerequisites(Map<String, String> data) {
        String[] prereq = {"lon", "lat", "status", "mc_accident_orig_type", "mc_accident_orig_med", "id", "address", "created", "owner_id", "owner",
                "descr"};
        for (String key : prereq) {
            if (!data.containsKey(key)) {
                Log.d("PARSE ERROR", data.get(key));
                return false;
            }
        }
        return true;
    }

    public String getMedText() {
        return Const.med_text.get(med);
    }

    public String getStatusText() {
        return Const.status_text.get(status);
    }

    public String getTypeText() {
        return Const.type_text.get(type);
    }

    public String getDistanceText() {
        double d = distanceFromUser();
        if (d > 1000) {
            return String.valueOf(Math.round(d / 10) / 100) + "км";
        } else {
            return String.valueOf((int) d) + "м";
        }
    }

    public double distanceFromUser() {
        return MCLocation.current.distanceTo(location);
    }

    public boolean isToday() {
        Calendar calendar = Calendar.getInstance();
        int now = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(created);
        int current = calendar.get(Calendar.DAY_OF_YEAR);
        return current == now;
    }

    public class MCPointException extends Exception {
        private static final long serialVersionUID = 1L;

        public MCPointException() {
            super();
        }
    }
}
