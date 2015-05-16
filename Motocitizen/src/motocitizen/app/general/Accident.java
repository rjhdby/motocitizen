package motocitizen.app.general;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

import motocitizen.MyApp;
import motocitizen.app.general.popups.AccidentListPopup;
import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.startup.MyPreferences;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;
import motocitizen.utils.NewID;

@SuppressLint({"UseSparseArrays", "RtlHardcoded"})
public class Accident {
    private MyApp myApp = null;
    public Map<String, String> attributes;
    public Map<Integer, AccidentMessage> messages;
    public Map<Integer, AccidentVolunteer> volunteers;
    public Map<Integer, AccidentHistory> history;
    private Location location;
    private boolean onway, inplace, leave, hasHere;

    private String type, med, address, owner, descr;

    private enum PointStatus {
        ACTIVE, HIDDEN, ENDED,
    }

    public PointStatus status;

    public Date created;

    private int id, owner_id;
    public int row_id;
    private Context context;

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return descr;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatusString() {
        if (status == PointStatus.ACTIVE)
            return "acc_status_act";
        if (status == PointStatus.HIDDEN)
            return "acc_status_hide";
        if (status == PointStatus.ENDED)
            return "acc_status_end";
        return "";
    }

    public String getTextToCopy() {
        StringBuilder res = new StringBuilder();
        res.append(Const.dateFormat.format(created)).append(". ");
        res.append(getTypeText()).append(". ");
        String med = getMedText();
        if (med.length() > 0) {
            res.append(med).append(". ");
        }
        res.append(address).append(". ");
        res.append(descr).append(".");
        return res.toString();
    }

    private MyPreferences prefs;

    private final View.OnClickListener accRowClick = new View.OnClickListener() {
        public void onClick(View v) {
            AccidentsGeneral.toDetails(v.getContext(), id);
        }
    };

    private final OnLongClickListener rowLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = AccidentListPopup.getPopupWindow(id, false);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    public Accident(Bundle extras, Context context) throws MCPointException {
        this.context = context;
        myApp = (MyApp) context.getApplicationContext();
        prefs = myApp.getPreferences();
        Map<String, String> data = new HashMap<>();
        for (String key : extras.keySet()) {
            data.put(key, extras.getString(key));
        }
        createPoint(data);
        makeHistory(null);
        makeMessages(null);
        makeVolunteers(null);
    }

    public Accident(JSONObject json, Context context) throws MCPointException {
        this.context = context;
        myApp = (MyApp) context.getApplicationContext();
        prefs = myApp.getPreferences();
        createPoint(buildDataSet(json));
        try {
            makeHistory(json.getJSONArray("history"));
            makeMessages(json.getJSONArray("messages"));
            makeVolunteers(json.getJSONArray("onway"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MCPointException();
        }
    }

    private void createPoint(Map<String, String> data) throws MCPointException {
        if (!checkPrerequisites(data))
            throw new MCPointException();
        attributes = new HashMap<>();

        try {
            location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(Float.parseFloat(data.get("lat")));
            location.setLongitude(Float.parseFloat(data.get("lon")));
            location.setAccuracy(0);
            type = data.get("mc_accident_orig_type");
            setStatus(data.get("status"));
            med = data.get("mc_accident_orig_med");
            id = Integer.parseInt(data.get("id"));
            address = data.get("address");
            created = Const.dateFormat.parse(data.get("created"));
            owner = data.get("owner");
            owner_id = Integer.parseInt(data.get("owner_id"));
            descr = data.get("descr");
            onway = false;
            inplace = false;
            leave = false;
            hasHere = false;
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

        } catch (Exception e) {
            e.printStackTrace();
            throw new MCPointException();
        }
    }

    private void setStatus(String status) {
        switch (status) {
            case "acc_status_act":
                this.status = PointStatus.ACTIVE;
                break;
            case "acc_status_end":
                this.status = PointStatus.ENDED;
                break;
            case "acc_status_hide":
                this.status = PointStatus.HIDDEN;
                break;
            default:
                //TODO Придумать, как сделать более правильно
                Log.e("Accident", "Unknown point status");
                this.status = PointStatus.HIDDEN;
                break;
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
        if (myApp.getMCAuth().isAnonim())
            return false;
        int user = myApp.getMCAuth().getID();
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
        if (json == null) {
            return;
        }
        Map<Integer, AccidentMessage> newMessages = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                AccidentMessage current = new AccidentMessage(json.getJSONObject(i), id);
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
        if (json == null) {
            return;
        }
//        Map<Integer, AccidentVolunteer> newVolunteers = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                AccidentVolunteer volunteer = new AccidentVolunteer(json.getJSONObject(i));
                volunteers.put(volunteer.id, volunteer);
                //newVolunteers.put(current.id, current);
                if (volunteer.id == myApp.getMCAuth().getID()) {
                    switch (volunteer.getStatus()) {
                        case ONWAY:
                            //TODO Зачем храним эту информацию в двух местах? Думаю, что надо убрать AccidentsGeneral.onway и т.д. заменив на getOnWay и т.д.
                            setOnWay(volunteer.id);
                            AccidentsGeneral.setOnWay(volunteer.id);
                            break;
                        case INPLACE:
                            setInPlace(volunteer.id);
                            AccidentsGeneral.setInPlace(volunteer.id);
                            break;
                        case LEAVE:
                            setLeave(volunteer.id);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        volunteers.putAll(newVolunteers);
    }

    private void makeHistory(JSONArray json) {
        if (history == null)
            history = new HashMap<>();
        if (json == null) {
            return;
        }
        history.clear();
        for (int i = 0; i < json.length(); i++) {
            try {
                AccidentHistory current = new AccidentHistory(json.getJSONObject(i), id);
                history.put(current.id, current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public FrameLayout createAccRow(Context context) {
        ViewGroup vg = (ViewGroup) ((Activity) context).findViewById(R.id.accListContent);
        FrameLayout fl = (FrameLayout) Const.getLayoutInflater(context).inflate(R.layout.accident_row, vg, false);
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
        time.setText(MyUtils.getIntervalFromNowInText(created));
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
                Log.d("PARSE ERROR", key);
                return false;
            }
        }
        return true;
    }

    public String getMedText() {
        return Const.med_text.get(med);
    }

    public String getStatusText() {
        //TODO Масло маслянное
        return Const.status_text.get(getStatusString());
    }

    public String getTypeText() {
        return Const.type_text.get(type);
    }

    public String getDistanceText() {
        Double dist = getDistanceFromUser();
        if (dist != null) {
            if (dist > 1000) {
                return String.valueOf(Math.round(dist / 10) / 100) + "км";
            } else {
                return String.valueOf(Math.round(dist)) + "м";
            }
        } else {
            //TODO Унести в ресурсы
            return "Не доступно";
        }
    }

    private Double getDistanceFromUser() {
        Location loc = MyLocationManager.getLocation(context);
        return (double) loc.distanceTo(location);
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

    public boolean isVisible() {
        Double dist = getDistanceFromUser();
        if (isHidden() && !Role.isModerator()) {
            return false;
        }
        return dist == null || (dist < prefs.getVisibleDistance() * 1000) && prefs.toShowAccType(type);
    }

    public boolean isActive() {
        return (status == PointStatus.ACTIVE);
    }

    public boolean isHidden() {
        return (status == PointStatus.HIDDEN);
    }

    public boolean isEnded() {
        return (status == PointStatus.ENDED);
    }

    public void setOnWay(int userId) {
        if (userId == 0) return;
        AccidentVolunteer user = volunteers.get(userId);
        if (user == null) {
            volunteers.put(userId, new AccidentVolunteer(userId, prefs.getLogin(), "onway"));
        } else {
            volunteers.get(userId).setStatus(AccidentVolunteer.Status.ONWAY);
        }
        onway = true;
        inplace = false;
        leave = false;
    }

    public void setInPlace(int userId) {
        if (userId == 0) return;
        AccidentVolunteer user = volunteers.get(userId);
        if (user == null) {
            volunteers.put(userId, new AccidentVolunteer(userId, prefs.getLogin(), "inplace"));
        } else {
            volunteers.get(userId).setStatus(AccidentVolunteer.Status.INPLACE);
        }
        onway = false;
        inplace = true;
        leave = false;
        hasHere = true;
    }

    public void setLeave(int userId) {
        if (userId == 0) return;
        AccidentVolunteer user = volunteers.get(userId);
        if (user == null) {
            volunteers.put(userId, new AccidentVolunteer(userId, prefs.getLogin(), "leave"));
        } else {
            volunteers.get(userId).setStatus(AccidentVolunteer.Status.LEAVE);
        }
        onway = false;
        inplace = false;
        leave = true;
    }

    public boolean isOnWay() {
        return onway;
    }

    public boolean isInPlace() {
        return inplace;
    }

    public boolean isLeave() {
        return leave;
    }

    public void resetStatus() {
        onway = false;
        inplace = false;
        leave = false;
    }
}
