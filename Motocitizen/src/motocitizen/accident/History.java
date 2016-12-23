package motocitizen.accident;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.content.HistoryAction;
import motocitizen.utils.Preferences;

public class History {
    private static final String[] prerequisites = {"id", "id_user", "owner", "action", "uxtime"};

    private int           id;
    private int           ownerId;
    private boolean       self;
    private boolean       noError;
    private String        owner;
    private Date          time;
    private HistoryAction action;

    History(JSONObject json) {
        noError = checkPrerequisites(json);
        if (noError) try {
            id = json.getInt("id");
            ownerId = json.getInt("id_user");
            owner = json.getString("owner");
            time = new Date(json.getLong("uxtime") * 1000);
            action = HistoryAction.parse(json.getString("action"));
            self = ownerId == Preferences.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            noError = false;
        }
    }

    private boolean checkPrerequisites(JSONObject message) {
        for (String field : prerequisites) {
            if (!message.has(field)) return false;
        }
        return true;
    }

    public String getActionString() {
        return action.toString();
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwner() {
        return owner;
    }

    public HistoryAction getAction() {
        return action;
    }

    public Date getTime() {
        return time;
    }

    boolean isNoError() {
        return noError;
    }
}
