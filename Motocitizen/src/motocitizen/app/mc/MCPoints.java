package motocitizen.app.mc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("UseSparseArrays")
public class MCPoints {
	public String error = "ok";
	private Map<Integer, MCPoint> points;

	public static final int NORMAL = R.drawable.accident_row_gradient;
	public static final int NORMAL_SELECTED = R.drawable.accident_row_gradient_selected;
	public static final int HIDE = R.drawable.accident_row_gradient_hide;
	public static final int HIDE_SELECTED = R.drawable.accident_row_gradient_selected_hide;
	public static final int ENDED = R.drawable.accident_row_gradient_ended;
	public static final int ENDED_SELECTED = R.drawable.accident_row_gradient_selected_ended;

	public MCPoints() {
		if (points == null) {
			points = new HashMap<Integer, MCPoint>();
		}
	}

	public MCPoint getPoint(int id) {
		return points.get(id);
	}

	public Set<Integer> keySet() {
		return points.keySet();
	}

	public void load() {
		Map<String, String> selector = new HashMap<String, String>();
		Location userLocation = MCLocation.current;
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
			parseJSON(new JSONCall("mcaccidents", "getlist", false).request(selector).getJSONArray("list"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parseJSON(JSONArray json) throws JSONException {
		for (int i = 0; i < json.length(); i++) {
			JSONObject acc = json.getJSONObject(i);
			try {
				MCPoint current = new MCPoint(acc);
				if (points.containsKey(current.id)) {
					current.messages.putAll(points.get(current.id).messages);
				}
				points.put(current.id, current);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	public int getFirstNonNull() {
		for (int i : points.keySet()) {
			if (points.get(i).row_id != 0) {
				return i;
			}
		}
		return 0;
	}

	public void setSelected(Context context, int id) {
		for (int i : points.keySet()) {
			MCPoint p = points.get(i);
			if (p.row_id == 0) {
				continue;
			}
			View row = ((Activity) context).findViewById(p.row_id);
			row.setBackgroundResource(getBackground(p.status, false));
		}
		MCPoint selected = points.get(id);
		if (selected == null) {
			return;
		}
		if (selected.row_id == 0) {
			int nnid = getFirstNonNull();
			if (nnid == 0) {
				return;
			} else {
				setSelected(context, nnid);
				MCAccidents.currentPoint = points.get(nnid);
				MCAccidents.redraw(context);
			}
		} else {
			View row = ((Activity) context).findViewById(selected.row_id);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			int margin = (int) (4 * Const.dp);
			lp.setMargins(margin, 0, margin, 0);
			row.setLayoutParams(lp);
			row.setPadding(0, margin, 0, margin);

			selected.resetMessagesUnreadFlag();
		}
	}

	public int getBackground(String status, boolean selected) {
		int bg = NORMAL;
		if (status.equals("acc_status_act")) {
			if (selected) {
				bg = NORMAL_SELECTED;
			}
		}
		if (status.equals("acc_status_end")) {
			if (selected) {
				bg = ENDED_SELECTED;
			} else {
				bg = ENDED;
			}
		}
		if (status.equals("acc_status_hide")) {
			if (selected) {
				bg = HIDE_SELECTED;
			} else {
				bg = HIDE;
			}
		}
		return bg;
	}
}
