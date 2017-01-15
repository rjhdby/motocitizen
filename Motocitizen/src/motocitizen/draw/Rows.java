package motocitizen.draw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.accident.Accident;
import motocitizen.content.Medicine;
import motocitizen.main.R;
import motocitizen.utils.MyUtils;
import motocitizen.utils.popups.AccidentListPopup;

public class Rows {
    /* constants */
    private static final int ACCIDENT_ROW_LAYOUT     = R.layout.accident_row;
    private static final int ACCIDENT_ROW_OWN_LAYOUT = R.layout.accident_row_i_was_here;

    private static final int ACCIDENT_ROW_ENDED      = R.drawable.accident_row_ended;
    private static final int ACCIDENT_ROW_HIDDEN     = R.drawable.accident_row_hidden;
    private static final int ACCIDENT_ROW_OWN_ENDED  = R.drawable.owner_accident_ended;
    private static final int ACCIDENT_ROW_OWN_HIDDEN = R.drawable.owner_accident_hidden;
    /* end constants */

    public static View getAccidentRow(final Context context, ViewGroup parent, final Accident accident) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ((TextView) accRow.findViewById(R.id.accident_row_time)).setText(MyUtils.getIntervalFromNowInText(context, accident.getTime()));
        ((TextView) accRow.findViewById(R.id.accident_row_unread)).setText(Html.fromHtml(msgText));

        int rowId = MyUtils.newId();
        //accident.setRowId(rowId);
        accRow.setId(rowId);
        accRow.setOnClickListener(v -> toDetails(context, accident.getId()));

        accRow.setOnLongClickListener(v -> {
            PopupWindow popupWindow;
            popupWindow = (new AccidentListPopup(context, accident.getId())).getPopupWindow(context);
            int viewLocation[] = new int[ 2 ];
            v.getLocationOnScreen(viewLocation);
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[ 0 ], viewLocation[ 1 ]);
            return true;
        });
        return accRow;
    }

    private static void toDetails(Context context, int id) {
        Intent intent = new Intent(context, AccidentDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("accidentID", id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
