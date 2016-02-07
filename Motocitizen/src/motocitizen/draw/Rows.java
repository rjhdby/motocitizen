package motocitizen.draw;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.accident.History;
import motocitizen.accident.Message;
import motocitizen.accident.Volunteer;
import motocitizen.app.general.popups.AccidentListPopup;
import motocitizen.app.general.user.Auth;
import motocitizen.content.Medicine;
import motocitizen.main.R;
import motocitizen.utils.Const;
import motocitizen.utils.MyUtils;

public class Rows {
    /* constants */
    private static final int ACCIDENT_ROW_LAYOUT     = R.layout.accident_row;
    private static final int ACCIDENT_ROW_OWN_LAYOUT = R.layout.accident_row_i_was_here;

    private static final int ACCIDENT_ROW_ENDED      = R.drawable.accident_row_ended;
    private static final int ACCIDENT_ROW_HIDDEN     = R.drawable.accident_row_hidden;
    private static final int ACCIDENT_ROW_OWN_ENDED  = R.drawable.owner_accident_ended;
    private static final int ACCIDENT_ROW_OWN_HIDDEN = R.drawable.owner_accident_hidden;
    /* end constants */

    public static View getAccidentRow(ViewGroup parent, final Accident accident) {
        LayoutInflater li = (LayoutInflater) MyApp.getCurrentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout    accRow;
        accRow = (FrameLayout) li.inflate(accident.isOwner() ? ACCIDENT_ROW_OWN_LAYOUT : ACCIDENT_ROW_LAYOUT, parent, false);
        switch (accident.getStatus()) {
            case ENDED:
                accRow.setBackgroundResource(accident.isOwner() ? ACCIDENT_ROW_OWN_ENDED : ACCIDENT_ROW_ENDED);
                break;
            case HIDDEN:
                accRow.setBackgroundResource(accident.isOwner() ? ACCIDENT_ROW_OWN_HIDDEN : ACCIDENT_ROW_HIDDEN);
        }
        StringBuilder generalText = new StringBuilder();
        generalText.append(accident.getType().toString());
        if (accident.getMedicine() != Medicine.UNKNOWN) {
            generalText.append(", ").append(accident.getMedicine().toString());
        }
        generalText.append("(").append(accident.getDistanceString()).append(")\n").append(accident.getAddress()).append("\n").append(accident.getDescription());
        String msgText = "<b>" + String.valueOf(accident.getMessages().size()) + "</b>";
        msgText += accident.getUnreadMessagesCount() > 0 ? "<font color=#C62828><b>(" + String.valueOf(accident.getUnreadMessagesCount()) + ")</b></font>" : "";

        switch (accident.getStatus()) {
            case ENDED:
                ((TextView) accRow.findViewById(R.id.accident_row_content)).setTextColor(0x70FFFFFF);
                break;
            case HIDDEN:
                ((TextView) accRow.findViewById(R.id.accident_row_content)).setTextColor(0x30FFFFFF);
        }

        ((TextView) accRow.findViewById(R.id.accident_row_content)).setText(generalText + " \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0");
        ((TextView) accRow.findViewById(R.id.accident_row_time)).setText(MyUtils.getIntervalFromNowInText(accident.getTime()));
        ((TextView) accRow.findViewById(R.id.accident_row_unread)).setText(Html.fromHtml(msgText));

        int rowId = MyUtils.newId();
        accident.setRowId(rowId);
        accRow.setId(rowId);
        accRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyApp.toDetails(accident.getId());
            }
        });
        accRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupWindow popupWindow;
                popupWindow = (new AccidentListPopup(accident.getId())).getPopupWindow();
                int viewLocation[] = new int[2];
                v.getLocationOnScreen(viewLocation);
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
                return true;
            }
        });
        return accRow;
    }

    private static View getYesterdayDelimiter(ViewGroup view) {
        LayoutInflater li = (LayoutInflater) MyApp.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return li.inflate(R.layout.yesterday_row, view, false);
    }

    public static View getVolunteerRow(ViewGroup viewGroup, Volunteer volunteer) {
        //TODO Header
        LayoutInflater li = (LayoutInflater) MyApp.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr = (TableRow) li.inflate(R.layout.volunteer_row, viewGroup, false);
        ((TextView) tr.findViewById(R.id.volunteer)).setText(volunteer.getName());
        ((TextView) tr.findViewById(R.id.action)).setText(volunteer.getStatus().toString());
        ((TextView) tr.findViewById(R.id.time)).setText(Const.TIME_FORMAT.format(volunteer.getTime()));
        return tr;
    }

    public static View getMessageRow(ViewGroup parent, final Message message, int last, int next) {
        LayoutInflater li = (LayoutInflater) MyApp.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View tr;
        int  user  = Auth.getInstance().getId();
        int  resource;
        int  owner = message.getOwnerId();
        resource = message.getOwnerId() == user ? R.layout.owner_message_row : R.layout.message_row;
        tr = li.inflate(resource, parent, false);
        FrameLayout              fl  = (FrameLayout) tr.findViewById(R.id.row);
        TableLayout.LayoutParams flp = (TableLayout.LayoutParams) fl.getLayoutParams();
        if (last == owner && next == owner) {
            fl.setBackgroundResource(R.drawable.message_row_middle);
            flp.topMargin = 0;
            fl.setLayoutParams(flp);
        } else if (next == owner && owner == user) {
            fl.setBackgroundResource(R.drawable.owner_message_row_first);
        } else if (next == owner) {
            fl.setBackgroundResource(R.drawable.message_row_first);
        } else if (last == owner) {
            fl.setBackgroundResource(R.drawable.message_row_last);
            flp.topMargin = 0;
            fl.setLayoutParams(flp);
        }

        TextView ownerView   = (TextView) tr.findViewById(R.id.owner);
        TextView messageView = (TextView) tr.findViewById(R.id.text);
        ownerView.setText(message.getOwner());
        StringBuilder messageText = new StringBuilder();
        if (owner == last) {
            ownerView.setVisibility(View.INVISIBLE);
        } else {
            messageText.append("\n");
        }
        messageText.append(message.getText());
        String timeText = Const.TIME_FORMAT.format(message.getTime());

        ((TextView) tr.findViewById(R.id.time)).setText(timeText);
        messageText.append(" \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0");
        messageView.setText(messageText);

        return tr;
    }

    public static View getHistoryRow(ViewGroup parent, History history) {
        //TODO header
        LayoutInflater li        = (LayoutInflater) MyApp.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow       tr        = (TableRow) li.inflate(R.layout.history_row, parent, false);
        TextView       ownerView = (TextView) tr.findViewById(R.id.owner);
        if (history.getOwnerId() == Auth.getInstance().getId()) {
            ownerView.setBackgroundColor(Color.DKGRAY);
        }
        ownerView.setText(history.getOwner());
        ((TextView) tr.findViewById(R.id.text)).setText(history.getActionString());
        ((TextView) tr.findViewById(R.id.date)).setText(MyUtils.getStringTime(history.getTime(), true));
        return tr;
    }
}
