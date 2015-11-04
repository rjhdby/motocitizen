package motocitizen.draw;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import motocitizen.MyApp;
import motocitizen.accident.History;
import motocitizen.main.R;
import motocitizen.utils.MyUtils;

public class HistoryRow {
    public static View makeView(Context context, ViewGroup parent, History history) {
        View row = LayoutInflater.from(context).inflate(R.layout.history_row, parent, false);

        TextView ownerView = (TextView) row.findViewById(R.id.owner);
        if (history.getOwnerId() == MyApp.getAuth().getId()) {
            ownerView.setBackgroundColor(Color.DKGRAY);
        }
        ownerView.setText(history.getOwner());
        ((TextView) row.findViewById(R.id.text)).setText(history.getActionString());
        ((TextView) row.findViewById(R.id.date)).setText(MyUtils.getStringTime(history.getTime(), true));
        return row;
    }
}
