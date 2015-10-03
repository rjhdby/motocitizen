package motocitizen.accident;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.content.MessageStatus;
import motocitizen.utils.Preferences;

public class Message {
    private static final String[] prerequisites = {"id", "id_user", "owner", "status", "text", "uxtime"};

    private int           id;
    private int           ownerId;
    private boolean       unread;
    private boolean       self;
    private boolean       noError;
    private String        owner;
    private String        text;
    private Date          time;
    private MessageStatus status;

    public Message(JSONObject json) {
        noError = checkPrerequisites(json);
        if (noError) try {
            id = json.getInt("id");
            ownerId = json.getInt("id_user");
            owner = json.getString("owner");
            status = MessageStatus.parse(json.getString("status"));
            text = json.getString("text");
            time = new Date(json.getLong("uxtime") * 1000);
            self = ownerId == Preferences.getUserId();
            unread = !self;

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

    public MessageStatus getStatus() {
        return status;
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

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isSelf() {
        return self;
    }

    public boolean isNoError() {
        return noError;
    }
}
