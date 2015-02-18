package motocitizen.core;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import motocitizen.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
	public int id;
	public int ownerId;
	public int entityId;
	public String owner;
	public String status;
	public String text;
	public String app;
	public Calendar modified = Calendar.getInstance();

	public Message(JSONObject json) {
		try {
			id = json.getInt("id");
			ownerId = json.getInt("id_user");
			owner = json.getString("owner");
			status = json.getString("status");
			text = json.getString("text");
			try {
				modified.setTime(Const.timeFormat.parse(json.getString("modified")));
			} catch (ParseException e) {
				modified.setTime(new Date());
			}
			try {
				entityId = json.getInt("id_ent");
			} catch (JSONException e) {
				entityId = 0;
			}
			try {
				app = json.getString("app");
			} catch (JSONException e) {
				app = "";
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
