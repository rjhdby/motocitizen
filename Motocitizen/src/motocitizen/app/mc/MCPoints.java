package motocitizen.app.mc;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import motocitizen.core.Point;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.location.Location;

@SuppressLint("UseSparseArrays")
public class MCPoints {
	public static String error = "ok";
	public static Map<Integer, Point> points;
	public static Map<Integer, MCMessages> messages;

	public MCPoints() {
		if (points == null) {
			points = new HashMap<Integer, Point>();
			messages = new HashMap<Integer, MCMessages>();
		}
	}

	public static Point findByCommonValue(String key, String value) {
		for (Point p : points.values()) {
			if (p.get(key).equals(value)) {
				return p;
			}
		}
		return null;
	}

	public static Point findByRowId(int id) {
		for (Point p : points.values()) {
			if (p.get("row_id").equals(String.valueOf(id))) {
				return p;
			}
		}
		return null;
	}

	public static Point get(int id) {
		return points.get(id);
	}

	public static double distanceFromUser(int id) {
		Location acc = new Location("");
		acc.setLatitude(Double.parseDouble(get(id).get("lat")));
		acc.setLongitude(Double.parseDouble(get(id).get("lon")));
		Location user = (Location) Startup.tasks.tasks.get("locationservice").getObj("getLocation");
		return user.distanceTo(acc);
	}

	public static String getTime(int id) {
		try {
			Calendar date = Calendar.getInstance();
			date.setTime(Const.dateFormat.parse(get(id).get("created")));
			return Const.timeFormat.format(date.getTime());
		} catch (ParseException e) {
			return "--:--";
		}
	}

	public static boolean isToday(int id) {
		Calendar calendar = Calendar.getInstance();
		int now = calendar.get(Calendar.DAY_OF_YEAR);
		try {
			calendar.setTime(Const.dateFormat.parse(get(id).get("created")));
		} catch (ParseException e) {
			return false;
		}
		if (now == calendar.get(Calendar.DAY_OF_YEAR)) {
			return true;
		} else {
			return false;
		}
	}

	public static void load() {
		Map<String, String> selector = new HashMap<String, String>();
		Location userLocation = (Location) Startup.tasks.tasks.get("locationservice").getObj("getLocation");
		selector.put("distance", String.valueOf(Startup.prefs.getInt("mc.distance.show", 100)));
		selector.put("lon", String.valueOf(userLocation.getLongitude()));
		selector.put("lat", String.valueOf(userLocation.getLatitude()));
		String user = Startup.prefs.getString("mc.login", "");
		if (!user.equals("")) {
			selector.put("user", user);
		}
		if (!points.isEmpty()) {
			selector.put("update", "1");
		}
		try {
			parseJSON(new JSONCall("mcaccidents", "getlist").request(selector).getJSONArray("list"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void parseJSON(JSONArray json) throws JSONException {
		for (int i = 0; i < json.length(); i++) {
			JSONObject acc = json.getJSONObject(i);
			if (acc.has("error")){
				error = acc.getString("error");
				return;}
			Map<String,String> dataset = new HashMap<String,String>();
			int id = acc.getInt("id");
			MCMessages m;
			try {
				m = new MCMessages(acc.getJSONArray("messages"));
				acc.remove("messages");
			} catch (JSONException e) {
				m = new MCMessages();
			}
			if (messages.containsKey(id)) {
				messages.get(id).messages.putAll(m.messages);
			} else {
				messages.put(id, m);
			}
			Iterator<String> keys = acc.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				dataset.put(key, acc.getString(key));
			}
			points.put(id, new Point(dataset));
		}
	}
}
