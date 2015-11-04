package motocitizen.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

import motocitizen.MyApp;
import motocitizen.accident.Message;
import motocitizen.main.R;
import motocitizen.utils.Const;

public class MessageRow {

    public static View makeView(Context context, ViewGroup parent, final Message message, int last, int next) {
        int user  = MyApp.getAuth().getId();
        int resource;
        int owner = message.getOwnerId();
        resource = message.getOwnerId() == user ? R.layout.owner_message_row : R.layout.message_row;
        View row = LayoutInflater.from(context).inflate(resource, parent, false);

        FrameLayout              fl  = (FrameLayout) row.findViewById(R.id.row);
        LinearLayout.LayoutParams  flp = (LinearLayout.LayoutParams) fl.getLayoutParams();
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

        TextView ownerView   = (TextView) row.findViewById(R.id.owner);
        TextView messageView = (TextView) row.findViewById(R.id.text);
        ownerView.setText(message.getOwner());
        StringBuilder messageText = new StringBuilder();
        if (owner == last) {
            ownerView.setVisibility(View.INVISIBLE);
        } else {
            messageText.append("\n");
        }
        messageText.append(message.getText());
        String timeText = Const.TIME_FORMAT.format(message.getTime());

        ((TextView) row.findViewById(R.id.time)).setText(timeText);
        messageText.append(" \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0");
        messageView.setText(messageText);
        return row;
    }
}
