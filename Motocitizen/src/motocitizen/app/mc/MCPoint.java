package motocitizen.app.mc;

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

import motocitizen.app.mc.popups.MCAccListPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.main.R;
import motocitizen.startup.MCPreferences;
import motocitizen.utils.Const;
import motocitizen.utils.MCUtils;
import motocitizen.utils.NewID;

import static motocitizen.app.mc.user.MCAuth.*;

@SuppressLint({"UseSparseArrays", "RtlHardcoded"})
public class MCPoint {
    public Map<String, String> attributes;
    public Map<Integer, MCMessage> messages;
    public Map<Integer, MCVolunteer> volunteers;
    public Map<Integer, MCPointHistory> history;
    private Location location;
    private boolean onway, inplace, leave, hashere;

    private String type, med, address, owner, descr;

    private enum PointStatus {
        ACTIVE, HIDEN, ENDED,
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
        if(status == PointStatus.ACTIVE)
            return "acc_status_act";
        if(status == PointStatus.HIDEN)
            return "acc_status_hide";
        if(status == PointStatus.ENDED)
            return "acc_status_end";
        return "";
    }

    public String getTextToCopy() {
        StringBuffer res = new StringBuffer();
        res.append(Const.dateFormat.format(created) + ". ");
        res.append(getTypeText() + ". ");
        String med = getMedText();
        if (med.length() > 0) {
            res.append(med + ". ");
        }
        res.append(address + ". ");
        res.append(descr + ".");
        return res.toString();
    }

    private MCPreferences prefs;

    private final View.OnClickListener accRowClick = new View.OnClickListener() {
        public void onClick(View v) {
            MCAccidents.toDetails(v.getContext(), id);
        }
    };

    private final OnLongClickListener rowLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCAccListPopup.getPopupWindow(id, false);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };

    public MCPoint(Bundle extras, Context context) throws MCPointException {
        this.context = context;
        prefs = new MCPreferences(context);
        Map<String, String> data = new HashMap<>();
        for (String key : extras.keySet()) {
            data.put(key, extras.getString(key));
        }
        createPoint(data);
        makeHistory(null);
        makeMessages(null);
        makeVolunteers(null);
    }

    public MCPoint(JSONObject json, Context context) throws MCPointException {
        this.context = context;
        prefs = new MCPreferences(context);
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
            hashere = false;
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
        if(status.equals("acc_status_act"))
            this.status = PointStatus.ACTIVE;
        else if(status.equals("acc_status_end"))
            this.status = PointStatus.ENDED;
        else if(status.equals("acc_status_hide"))
            this.status = PointStatus.HIDEN;
        else {
            //TODO Придумать, как сделать более правильно
            Log.e("MCPoint", "Unkown point status");
            this.status = PointStatus.HIDEN;
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
        if (MCAccidents.auth.isAnonim())
            return false;
        int user = MCAccidents.auth.getID();
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
        Map<Integer, MCMessage> newMessages = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                MCMessage current = new MCMessage(json.getJSONObject(i), id);
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
//        Map<Integer, MCVolunteer> newVolunteers = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            try {
                MCVolunteer current = new MCVolunteer(json.getJSONObject(i));
                volunteers.put(current.id, current);
                //newVolunteers.put(current.id, current);
                if(current.id == MCAccidents.auth.getID()){
                    if(current.status.equals("onway")){
                        setOnWay();
                        MCAccidents.onway = current.id;
                    } else if(current.status.equals("inplace")){
                        setInPlace();
                        MCAccidents.inplace = current.id;
                    } else if(current.status.equals("leave")){
                        setLeave();
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
                MCPointHistory current = new MCPointHistory(json.getJSONObject(i), id);
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
        Location loc = MCLocation.getLocation(context);
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
        if (dist == null) {
            return true;
        } else {
            return (dist < prefs.getVisibleDistance() * 1000) && prefs.toShowAccType(type);
        }
    }

    public boolean isActive() {
        return  (status == PointStatus.ACTIVE);
    }

    public boolean isHidden() {
        return  (status == PointStatus.HIDEN);
    }

    public boolean isEnded() {
        return  (status == PointStatus.ENDED);
    }

    public void setOnWay(){
        int userId = MCAccidents.auth.getID();
        if ( userId == 0 ) return;
        MCVolunteer user = volunteers.get(userId);
        if(user == null){
            volunteers.put(userId, new MCVolunteer(userId, prefs.getLogin(), "onway"));
        } else {
            volunteers.get(userId).status = "onway";
        }
        onway = true;
        inplace = false;
        leave = false;
    }

    public void setInPlace(){
        int userId = MCAccidents.auth.getID();
        if ( userId == 0 ) return;
        MCVolunteer user = volunteers.get(userId);
        if(user == null){
            volunteers.put(userId, new MCVolunteer(userId, prefs.getLogin(), "inplace"));
        } else {
            volunteers.get(userId).status = "inplace";
        }
        onway = false;
        inplace = true;
        leave = false;
        hashere = true;
    }

    public void setLeave(){
        int userId = MCAccidents.auth.getID();
        if ( userId == 0 ) return;
        MCVolunteer user = volunteers.get(userId);
        if(user == null){
            volunteers.put(userId, new MCVolunteer(userId, prefs.getLogin(), "leave"));
        } else {
            volunteers.get(userId).status = "leave";
        }
        onway = false;
        inplace = false;
        leave = true;
    }

    public boolean isOnWay(){
        return onway;
    }

    public boolean isInPlace(){
        return inplace;
    }

    public boolean isLeave(){
        return leave;
    }

    public void resetStatus(){
        onway = false;
        inplace = false;
        leave = false;
    }
}
