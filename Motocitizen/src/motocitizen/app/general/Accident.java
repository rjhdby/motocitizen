package motocitizen.app.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
    private Map<String, String>             attributes;
    public  Map<Integer, AccidentMessage>   messages;
    public  Map<Integer, AccidentVolunteer> volunteers;
    public  Map<Integer, AccidentHistory>   history;
    private Location                        location;
    private boolean                         onway, inplace, leave, hasHere, onway_cancel;

    private String type, med, address, owner, descr;

    private enum PointStatus {
        ACTIVE, HIDDEN, ENDED,
    }

    private PointStatus status;

    public Date created;

    private int id, owner_id;
    public        int     row_id;
    private final Context context;

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

    public int getOwnerId() {
        return owner_id;
    }

    private String getStatusString() {
        switch (status) {
            case ACTIVE:
                return "acc_status_act";
            case HIDDEN:
                return "acc_status_hide";
            case ENDED:
                return "acc_status_end";
            default:
                return "";
        }
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

    private final MyPreferences prefs;

    private final View.OnClickListener accRowClick = new View.OnClickListener() {
        public void onClick(View v) {
            AccidentsGeneral.toDetails(v.getContext(), id);
        }
    };

    public Accident(Bundle extras, Context context) throws MCPointException {
        this.context = context;
        myApp = (MyApp) context.getApplicationContext();
        prefs = myApp.getPreferences();
        Map<String, String> data = new HashMap<>();
        for (String key : extras.keySet()) {
            Object obj = extras.get(key);
            if (obj instanceof Integer) {
                data.put(key, String.valueOf(obj));
            } else {
                data.put(key, (String) obj);
            }
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
            onway_cancel = false;
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

    public void setStatus(String status) {
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
        Iterator<String>    keys = json.keys();
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

    private boolean hasInOwners() {
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
                AccidentMessage currentMsg = new AccidentMessage(json.getJSONObject(i), id);
                AccidentMessage oldMsg = messages.get(currentMsg.id);
                if (oldMsg != null)
                    // Если такое сообщение уже существует, извлекаем из него флаг прочтения
                    currentMsg.unread = oldMsg.unread;
                newMessages.put(currentMsg.id, currentMsg);
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
        for (int i = 0; i < json.length(); i++) {
            try {
                AccidentVolunteer volunteer = new AccidentVolunteer(json.getJSONObject(i));
                volunteers.put(volunteer.id, volunteer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public void inflateRow(final Context context, ViewGroup viewGroup) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout    fl = (FrameLayout) li.inflate(R.layout.accident_row, viewGroup, false);

        StringBuilder generalText = new StringBuilder();
        generalText.append(getTypeText());
        if (!med.equals("mc_m_na")) {
            generalText.append(", ").append(getMedText());
        }
        generalText.append("(").append(getDistanceText()).append(")\n").append(address).append("\n").append(descr);
        String msgText = "<b>" + String.valueOf(messages.size()) + "</b>";
        if (countUnreadMessages() > 0)
            msgText += "<font color=#C62828><b>(" + String.valueOf(countUnreadMessages()) + ")</b></font>";

        ((TextView) fl.findViewById(R.id.accident_row_content)).setText(generalText);
        ((TextView) fl.findViewById(R.id.accident_row_time)).setText(MyUtils.getIntervalFromNowInText(created));
        ((TextView) fl.findViewById(R.id.accident_row_unread)).setText(Html.fromHtml(msgText));
        if (hasInOwners()) (fl.findViewById(R.id.i_was_here)).setBackgroundColor(0xFFC62828);

        fl.setId(NewID.id());
        fl.setBackgroundResource(Accidents.getBackground(getStatusString()));
        fl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AccidentsGeneral.toDetails(v.getContext(), id);
            }
        });
        fl.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupWindow popupWindow;
                popupWindow = (new AccidentListPopup(context, id, false)).getPopupWindow();
                int viewLocation[] = new int[2];
                v.getLocationOnScreen(viewLocation);
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
                return true;
            }
        });
        viewGroup.addView(fl);
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
        int      now      = calendar.get(Calendar.DAY_OF_YEAR);
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

    public boolean isInvisible() {
        Double dist = getDistanceFromUser();
        return isHidden() && !Role.isModerator() || (dist != null && ((dist >= prefs.getVisibleDistance() * 1000) || prefs.toHideAccType(type)));
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

        if (volunteers.get(userId) == null) {
            volunteers.put(userId, new AccidentVolunteer(userId, prefs.getLogin(), AccidentVolunteer.Status.ONWAY));
        } else {
            volunteers.get(userId).setStatus(AccidentVolunteer.Status.ONWAY);
        }
        onway = true;
        inplace = false;
        leave = false;
    }

    public void setCancelOnWay(int userId) {
        AccidentVolunteer volunteer = volunteers.get(userId);
        if (volunteer == null) return;
        volunteer.setStatus(AccidentVolunteer.Status.CANCEL);
        onway = false;
        inplace = false;
        leave = false;
        onway_cancel = true;
    }

    public void setInPlace(int userId) {
        if (userId == 0) return;
        AccidentVolunteer user = volunteers.get(userId);
        if (user == null) {
            volunteers.put(userId, new AccidentVolunteer(userId, prefs.getLogin(), AccidentVolunteer.Status.INPLACE));
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
            volunteers.put(userId, new AccidentVolunteer(userId, prefs.getLogin(), AccidentVolunteer.Status.LEAVE));
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
        onway_cancel = false;
    }

    public boolean isOnwayCancel() {
        return onway_cancel;
    }

    public int getHoursAgo() {
        return (int) ((new Date()).getTime() - created.getTime()) / 3600000;
    }
}
