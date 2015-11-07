package motocitizen.draw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import motocitizen.accident.Accident;
import motocitizen.accident.Volunteer;
import motocitizen.main.R;
import motocitizen.utils.MyUtils;

public class VolunteerListAdapter extends BaseAdapter {
    Activity             activity;
    ArrayList<Volunteer> volunteers;

    {
        volunteers = new ArrayList<>();
    }

    public VolunteerListAdapter(Activity activity, Accident accident) {
        super();
        this.activity = activity;
        for (int id : accident.getVolunteers().sortedKeySet()) {
            volunteers.add(accident.getVolunteer(id));
        }
    }

    @Override
    public int getCount() {
        return volunteers.size();
    }

    @Override
    public Object getItem(int i) {
        return volunteers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater  = activity.getLayoutInflater();
        Volunteer      volunteer = volunteers.get(i);
        if (view == null) {
            view = inflater.inflate(R.layout.volunteer_list_row, null);
        }
        ((TextView) view.findViewById(R.id.volunteer)).setText(volunteer.getName());
        ((TextView) view.findViewById(R.id.action)).setText(volunteer.getStatus().toString());
        ((TextView) view.findViewById(R.id.time)).setText(MyUtils.getStringTime(volunteer.getTime()));
        return view;
    }
}
