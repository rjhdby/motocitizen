package motocitizen.app.general;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.app.general.popups.MessagesPopup;
import motocitizen.main.R;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;

public class AccidentMessage {
    public       int    id;
    public       int    owner_id;
    public final int    acc_id;
    public       String owner, status, text;
    public         Date    time;
    public         Boolean unread;
    private static Context context;

    public AccidentMessage(JSONObject json, int acc_id) throws JSONException {
        this.acc_id = acc_id;
        unread = true;
        id = json.getInt("id");
        owner_id = json.getInt("id_user");
        owner = json.getString("owner");
        status = json.getString("status");
        text = json.getString("text");
        time = new Date(Long.parseLong(json.getString("uxtime"), 10) * 1000);
    }
/*
    public TableRow createRow(Context context, String login) {
        this.context = context;
        TableRow              tr      = new TableRow(context);
        TableRow.LayoutParams lp      = new TableRow.LayoutParams();
        TextView              tvTime  = new TextView(tr.getContext());
        TextView              tvOwner = new TextView(tr.getContext());
        TextView              tvText  = new TextView(tr.getContext());
        lp.setMargins(0, 0, 5, 0);
        tvTime.setText(Const.timeFormat.format(time.getTime()));
        tvOwner.setLayoutParams(lp);
        tvOwner.setText(owner);
        if (owner.equals(login)) {
            tvOwner.setBackgroundColor(Color.DKGRAY);
        } else {
            tvOwner.setBackgroundColor(Color.GRAY);
        }
        tvText.setMaxLines(10);
        tvText.setSingleLine(false);
        tvText.setText(text);
        tr.setTag(String.valueOf(id));
        tr.addView(tvTime);
        tr.addView(tvOwner);
        tr.addView(tvText);
        tr.setOnLongClickListener(rowLongClick);
        return tr;
    }
    */
    public void inflateRow(Context context, ViewGroup tableLayout) {
        LayoutInflater li        = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr        = (TableRow) li.inflate(R.layout.message_row, tableLayout, false);
        TextView       ownerView = (TextView) tr.findViewById(R.id.owner);
        if (owner.equals(AccidentsGeneral.auth.getLogin())) {
            ownerView.setBackgroundColor(Color.DKGRAY);
        }
        ownerView.setText(owner);
        ((TextView) tr.findViewById(R.id.text)).setText(text);
        ((TextView) tr.findViewById(R.id.time)).setText(Const.timeFormat.format(time.getTime()));
        tr.setOnLongClickListener(rowLongClick);
        tableLayout.addView(tr);
    }

    private final OnLongClickListener rowLongClick = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            PopupWindow popupWindow;
            popupWindow = (new MessagesPopup(context, id, acc_id)).getPopupWindow();
            int viewLocation[] = new int[2];
            v.getLocationOnScreen(viewLocation);
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
            return true;
        }
    };
}
