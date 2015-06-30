package motocitizen.app.general;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import motocitizen.app.general.popups.MessagesPopup;
import motocitizen.main.R;
import motocitizen.utils.Const;

public class AccidentMessage {
    public        int     id;
    public        int     owner_id;
    private final int     acc_id;
    public        String  owner;
    private       String  status;
    public        String  text;
    public        Date    time;
    public        Boolean unread;

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

    public void inflateRow(final Context context, ViewGroup tableLayout, String last, String next) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View   tr;
        String user = AccidentsGeneral.auth.getLogin();
        if (owner.equals(user)) {
            tr = li.inflate(R.layout.owner_message_row, tableLayout, false);
        } else {
            tr = li.inflate(R.layout.message_row, tableLayout, false);
        }
        FrameLayout              fl  = (FrameLayout) tr.findViewById(R.id.row);
        TableLayout.LayoutParams flp = (TableLayout.LayoutParams) fl.getLayoutParams();
        if (last.equals(owner) && next.equals(owner)) {
            fl.setBackgroundResource(R.drawable.message_row_middle);
            flp.topMargin = 0;
            fl.setLayoutParams(flp);
        } else if (next.equals(owner) && owner.equals(user)) {
            fl.setBackgroundResource(R.drawable.owner_message_row_first);
        } else if (next.equals(owner)) {
            fl.setBackgroundResource(R.drawable.message_row_first);
        } else if (last.equals(owner)) {
            fl.setBackgroundResource(R.drawable.message_row_last);
            flp.topMargin = 0;
            fl.setLayoutParams(flp);
        }

        TextView ownerView   = (TextView) tr.findViewById(R.id.owner);
        TextView messageView = (TextView) tr.findViewById(R.id.text);
        ownerView.setText(owner);
        StringBuilder messageText = new StringBuilder();
        if (owner.equals(last)) {
            ownerView.setVisibility(View.INVISIBLE);
        } else {
            messageText.append("\n");
        }
        messageText.append(text);
        String timeText = Const.timeFormat.format(time.getTime());

        ((TextView) tr.findViewById(R.id.time)).setText(timeText);
        messageText.append(" \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0");
        messageView.setText(messageText);
        tableLayout.addView(tr);
        tr.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                PopupWindow popupWindow;
                popupWindow = (new MessagesPopup(context, id, acc_id)).getPopupWindow();
                int viewLocation[] = new int[2];
                v.getLocationOnScreen(viewLocation);
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
                return true;
            }
        });
    }
}
