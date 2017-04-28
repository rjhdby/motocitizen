package motocitizen.content;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class Message {
    private static final String[] prerequisites = {"id", "id_user", "owner", "status", "text", "uxtime"};

    private int     id;
    private int     ownerId;
    private boolean unread;
    private boolean noError;
    private String  owner;
    private String  text;
    private Date    time;

    Message(JSONObject json) {
        noError = checkPrerequisites(json);
        if (noError) try {
            id = json.getInt("id");
            ownerId = json.getInt("id_user");
            owner = json.getString("owner");
            text = json.getString("text");
            time = new Date(json.getLong("uxtime") * 1000);
            unread = ownerId != Preferences.getInstance().getUserId();

        } catch (Exception e) {
            noError = false;
            e.printStackTrace();
        }
    }

    private boolean checkPrerequisites(JSONObject message) {
        for (String field : prerequisites) {
            if (!message.has(field)) return false;
        }
        return true;
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

    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setRead() {
        this.unread = false;
    }

    public boolean isNoError() {
        return noError;
    }

    public boolean isOwner() {
        return ownerId == User.getInstance().getId();
    }
}
