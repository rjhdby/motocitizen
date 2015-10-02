package motocitizen.accident;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.content.VolunteerStatus;
import motocitizen.utils.Preferences;

public class Volunteer {
    private int             id;
    private String          name;
    private VolunteerStatus status;
    private Date            time;
    private boolean         self;
    private boolean         noError;

    private static final String[] prerequisites = {"id", "name", "status", "uxtime"};

    public Volunteer(JSONObject json) {
        noError = checkPrerequisites(json);
        if (noError) try {
            id = json.getInt("id");
            name = json.getString("name");
            status = VolunteerStatus.parse(json.getString("status"));
            time = new Date(json.getLong("uxtime") * 1000);
            self = id == Preferences.getUserId();
        } catch (Exception e) {
            noError = false;
            e.printStackTrace();
        }
    }

    private static boolean checkPrerequisites(JSONObject message) {
        for (String field : prerequisites) {
            if (!message.has(field)) return false;
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VolunteerStatus getStatus() {
        return status;
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
