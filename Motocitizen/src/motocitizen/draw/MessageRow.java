package motocitizen.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import motocitizen.accident.Message;
import motocitizen.user.User;
import motocitizen.main.R;
import motocitizen.utils.DateUtils;

public class MessageRow extends FrameLayout {

    public MessageRow(Context context, final Message message, int last, int next) {
        super(context);

        int user  = User.getInstance().getId();
        int resource;
        int owner = message.getOwnerId();
        resource = message.getOwnerId() == user ? R.layout.owner_message_row : R.layout.message_row;
        LayoutInflater.from(context).inflate(resource, this, true);

        FrameLayout              fl  = (FrameLayout) this.findViewById(R.id.row);
        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) fl.getLayoutParams();
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

        TextView ownerView   = (TextView) this.findViewById(R.id.owner);
        TextView messageView = (TextView) this.findViewById(R.id.text);
        ownerView.setText(message.getOwner());
        StringBuilder messageText = new StringBuilder();
        if (owner == last) {
            ownerView.setVisibility(View.INVISIBLE);
        } else {
            messageText.append("\n");
        }
        messageText.append(message.getText());
        String timeText = DateUtils.getTime(message.getTime());

        ((TextView) this.findViewById(R.id.time)).setText(timeText);
        messageText.append(" \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0");
        messageView.setText(messageText);
    }
}
