package motocitizen.app.mc;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.popups.MCMessagesPopup;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.utils.MCUtils;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

public class MCPointHistory {
	public int id, owner_id, table_row, acc_id;
	public String owner, action;
	public Date time;
	private static final Map<String, String> actions;
	static {
		Map<String, String> m = new HashMap<String, String>();
		m.put("create_mc_acc", "создал");
		m.put("onway", "выехал");
		m.put("inplace", "приехал");
		m.put("leave", "уехал");
		m.put("finish_mc_acc", "отбой");
		m.put("acc_status_hide", "скрыл");
		m.put("acc_status_act", "открыл");
		m.put("acc_status_end", "отбой");
		actions = Collections.unmodifiableMap(m);
	}

	public MCPointHistory(JSONObject json, int acc_id) throws JSONException {
		this.acc_id = acc_id;
		id = json.getInt("id");
		owner_id = json.getInt("id_user");
		owner = json.getString("owner");
		action = json.getString("action");
		try {
			time = Const.dateFormat.parse(json.getString("time"));
		} catch (ParseException e) {
			time = new Date();
		}
	}

	public String getAction() {
		if (actions.containsKey(action)) {
			return actions.get(action);
		} else {
			return "другое";
		}
	}

	public static TableRow createHeader(Context context) {
		TableRow tr = new TableRow(context);
		TableRow.LayoutParams lp = new TableRow.LayoutParams();
		TextView tvOwner = new TextView(tr.getContext());
		TextView tvText = new TextView(tr.getContext());
		TextView tvDate = new TextView(tr.getContext());
		lp.setMargins(0, 0, 5, 0);
		tvOwner.setLayoutParams(lp);
		tvOwner.setText("Кто");
		tvText.setText("Что");
		tvDate.setText("Когда");
		tr.addView(tvOwner);
		tr.addView(tvText);
		tr.addView(tvDate);
		return tr;
	}

	public TableRow createRow(Context context) {
		TableRow tr = new TableRow(context);
		TableRow.LayoutParams lp = new TableRow.LayoutParams();
		TextView tvOwner = new TextView(tr.getContext());
		TextView tvText = new TextView(tr.getContext());
		TextView tvDate = new TextView(tr.getContext());
		lp.setMargins(0, 0, 5, 0);
		tvOwner.setLayoutParams(lp);
		tvOwner.setText(owner);
		if (owner.equals(MCAccidents.auth.getLogin())) {
			tvOwner.setBackgroundColor(Color.DKGRAY);
		} else {
			tvOwner.setBackgroundColor(Color.GRAY);
		}
		tvText.setText(getAction());
		tvDate.setText(MCUtils.getStringTime(time, true));
		tr.setTag(String.valueOf(id));
		tr.addView(tvOwner);
		tr.addView(tvText);
		tr.addView(tvDate);
		// tr.setOnLongClickListener(rowLongClick);
		return tr;
	}

	private OnLongClickListener rowLongClick = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			PopupWindow pw;
			pw = MCMessagesPopup.getPopupWindow(id, acc_id);
			pw.showAsDropDown(v, 20, -20);
			return true;
		}
	};
}
