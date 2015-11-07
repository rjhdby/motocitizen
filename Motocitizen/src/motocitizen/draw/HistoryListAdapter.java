package motocitizen.draw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import motocitizen.MyApp;
import motocitizen.accident.Accident;
import motocitizen.accident.History;
import motocitizen.main.R;
import motocitizen.utils.MyUtils;

public class HistoryListAdapter extends BaseAdapter {
    Activity           activity;
    ArrayList<History> history;

    {
        history = new ArrayList<>();
    }

    public HistoryListAdapter(Activity activity, Accident accident) {
        super();
        this.activity = activity;
        for (int id : accident.getHistory().sortedKeySet()) {
            history.add(accident.getHistory().get(id));
        }
    }

    @Override
    public int getCount() {
        return history.size();
    }

    @Override
    public Object getItem(int i) {
        return history.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();
        History        h        = history.get(i);
        if (view == null) {
            view = inflater.inflate(R.layout.history_list_row, null);
        }
        ((TextView) view.findViewById(R.id.owner)).setText(h.getOwner());
        if (h.getOwnerId() == MyApp.getAuth().getId())
            ((TextView) view.findViewById(R.id.owner)).setTextColor(0xffffff00);
        ((TextView) view.findViewById(R.id.text)).setText(h.getActionString());
        ((TextView) view.findViewById(R.id.date)).setText(MyUtils.getStringTime(h.getTime()));
        return view;
    }
}
