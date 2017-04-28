package motocitizen.content;

import org.json.JSONObject;

import java.util.Date;

import motocitizen.dictionary.VolunteerStatus;

public class Volunteer {
    private static final String[] prerequisites = { "id", "name", "status", "uxtime" };

    private int             id;
    private boolean         noError;
    private String          name;
    private Date            time;
    private VolunteerStatus status;

    Volunteer(JSONObject json) {
        noError = checkPrerequisites(json);
        if (noError) try {
            id = json.getInt("id");
            name = json.getString("name");
            status = VolunteerStatus.parse(json.getString("status"));
            time = new Date(json.getLong("uxtime") * 1000);
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

    public String getName() {
        return name;
    }

    public VolunteerStatus getStatus() {
        return status;
    }

    public Date getTime() {
        return time;
    }

    public boolean isNoError() {
        return noError;
    }
}
