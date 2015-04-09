package motocitizen.app.mc;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import motocitizen.app.mc.popups.MCMessagesPopup;
import motocitizen.utils.Const;

public class MCMessage {
    public int id;
    public int owner_id;
    public int table_row;
    public final int acc_id;
    private final OnLongClickListener rowLongClick = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            PopupWindow pw;
            pw = MCMessagesPopup.getPopupWindow(id, acc_id);
            pw.showAsDropDown(v, 20, -20);
            return true;
        }
    };
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
        text = json.getString("text");
        time = new Date(Long.parseLong(json.getString("uxtime"), 10)*1000);
    }

    public TableRow createRow(Context context) {
        TableRow tr = new TableRow(context);
        TableRow.LayoutParams lp = new TableRow.LayoutParams();
        TextView tvDate = new TextView(tr.getContext());
        TextView tvOwner = new TextView(tr.getContext());
        TextView tvText = new TextView(tr.getContext());
        lp.setMargins(0, 0, 5, 0);

        tvDate.setText(Const.timeFormat.format(time.getTime()));
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
        tr.addView(tvDate);
        tr.addView(tvOwner);
        tr.addView(tvText);
        tr.setOnLongClickListener(rowLongClick);
        return tr;
    }
}
