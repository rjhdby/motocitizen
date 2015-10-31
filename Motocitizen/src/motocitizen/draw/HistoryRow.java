package motocitizen.draw;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import motocitizen.MyApp;
import motocitizen.accident.History;
import motocitizen.main.R;
import motocitizen.utils.MyUtils;

public class HistoryRow extends LinearLayout {
    public HistoryRow(Context context, History history) {
        super(context);
        setLayoutParams(generateDefaultLayoutParams());
        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.history_row, this, true);

        TextView ownerView = (TextView) this.findViewById(R.id.owner);
        if (history.getOwnerId() == MyApp.getAuth().getId()) {
            ownerView.setBackgroundColor(Color.DKGRAY);
        }
        ownerView.setText(history.getOwner());
        ((TextView) this.findViewById(R.id.text)).setText(history.getActionString());
        ((TextView) this.findViewById(R.id.date)).setText(MyUtils.getStringTime(history.getTime(), true));
    }

    public HistoryRow(Context context) {
        super(context);
    }

    public HistoryRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
