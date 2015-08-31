package motocitizen.accident;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.content.HistoryAction;
import motocitizen.startup.Preferences;

public class History {
    private static final String[] prerequisites = {"id", "id_user", "owner", "action", "uxtime"};
    private int           id;
    private int           owner_id;
    private String        owner;
    private HistoryAction action;
    private String        actionText;
    private Date          time;
    private boolean       self;
    private boolean       noError;

    public History(JSONObject json) {
        noError = checkPrerequisites(json);
        if (noError) try {
            id = json.getInt("id");
            owner_id = json.getInt("id_user");
            owner = json.getString("owner");
            time = new Date(json.getLong("uxtime") * 1000);
            actionText = json.getString("action");
            action = HistoryAction.parse(json.getString("action"));
            self = owner_id == Preferences.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            noError = false;
        }
    }

    private static boolean checkPrerequisites(JSONObject message) {
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

    public int getOwner_id() {
        return owner_id;
    }

    public String getOwner() {
        return owner;
    }

    public HistoryAction getAction() {
        return action;
    }

    public String getActionText() {
        return actionText;
    }

    public Date getTime() {
        return time;
    }

    public boolean isSelf() {
        return self;
    }

    public boolean isNoError() {
        return noError;
    }
}
