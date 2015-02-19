package motocitizen.app.mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import motocitizen.app.mc.user.MCAuth;
import motocitizen.core.Message;
import motocitizen.startup.Startup;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class MCMessages {
	public Map<Integer, Message> messages;
	public int id;

	public MCMessages(JSONArray json) {
		messages = new HashMap<Integer, Message>();
		for (int i = 0; i < json.length(); i++) {
			try {
				Integer id = json.getJSONObject(i).getInt("id");
				messages.put(id, new Message(json.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public MCMessages() {
		messages = new HashMap<Integer, Message>();
	}

	public List<Integer> getOrder() {
		List<Integer> l = new ArrayList<Integer>();
		l.addAll(messages.keySet());
		Collections.reverse(l);
		return l;
	}

	public TableRow createRow(int id) {
		return createRow(id, Startup.context);
	}

	public TableRow createRow(int id, Context cont) {
		TableRow tr = new TableRow(cont);
		TableRow.LayoutParams lp = new TableRow.LayoutParams();
		TextView tvOwner = new TextView(tr.getContext());
		TextView tvText = new TextView(tr.getContext());
		lp.setMargins(0, 0, 5, 0);
		tvOwner.setLayoutParams(lp);
		tvOwner.setText(messages.get(id).owner);
		if (messages.get(id).owner.equals(MCAuth.user.get("login"))) {
			tvOwner.setBackgroundColor(Color.LTGRAY);
		} else {
			tvOwner.setBackgroundColor(Color.GRAY);
		}
		tvText.setMaxLines(10);
		tvText.setSingleLine(false);
		tvText.setText(messages.get(id).text);
		tr.addView(tvOwner);
		tr.addView(tvText);
		return tr;
	}
	
	public void drawList(ViewGroup vg){
		vg.removeAllViews();
		Integer[] order = new Integer[messages.size()];
		messages.keySet().toArray(order);
		Arrays.sort(order, Collections.reverseOrder());
		for (int i : order) {
			vg.addView(createRow(i, vg.getContext()));
		}
	}
}
