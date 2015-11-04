package motocitizen.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import motocitizen.accident.Volunteer;
import motocitizen.main.R;
import motocitizen.utils.Const;

public class VolunteerRow {
    public static View makeView(Context context, ViewGroup parent, Volunteer volunteer) {
        View row = LayoutInflater.from(context).inflate(R.layout.volunteer_row, parent, false);
        ((TextView) row.findViewById(R.id.volunteer)).setText(volunteer.getName());
        ((TextView) row.findViewById(R.id.action)).setText(volunteer.getStatus().toString());
        ((TextView) row.findViewById(R.id.time)).setText(Const.TIME_FORMAT.format(volunteer.getTime()));
        return row;
    }
}
