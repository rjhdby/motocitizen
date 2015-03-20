package motocitizen.app.mc;

import java.text.ParseException;
import java.util.Date;

import motocitizen.app.mc.popups.MCMessagesPopup;
import motocitizen.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

public class MCMessage {
	public int id, owner_id, table_row, acc_id;
	public String owner, status, text;
	public Date time;
	public Boolean unread;

	public MCMessage(JSONObject json, int acc_id) throws JSONException {
		this.acc_id = acc_id;
		unread = true;
		id = json.getInt("id");
		owner_id = json.getInt("id_user");
		owner = json.getString("owner");
		status = json.getString("status");
		text = json.getString("text");
		try {
			time = Const.dateFormat.parse(json.getString("modified"));
		} catch (ParseException e) {
			time = new Date();
		}
	}

	public TableRow createRow(Context context) {
		TableRow tr = new TableRow(context);
		TableRow.LayoutParams lp = new TableRow.LayoutParams();
		TextView tvOwner = new TextView(tr.getContext());
		TextView tvText = new TextView(tr.getContext());
		lp.setMargins(0, 0, 5, 0);
		tvOwner.setLayoutParams(lp);
		tvOwner.setText(owner);
		if (owner.equals(MCAccidents.auth.getLogin())) {
			tvOwner.setBackgroundColor(Color.DKGRAY);
		} else {
			tvOwner.setBackgroundColor(Color.GRAY);
		}
		tvText.setMaxLines(10);
		tvText.setSingleLine(false);
		tvText.setText(text);
		tr.setTag(String.valueOf(id));
		tr.addView(tvOwner);
		tr.addView(tvText);
		tr.setOnLongClickListener(rowLongClick);
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
