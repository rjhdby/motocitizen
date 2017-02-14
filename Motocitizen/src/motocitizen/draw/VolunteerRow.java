package motocitizen.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TableRow;
import android.widget.TextView;

import motocitizen.accident.Volunteer;
import motocitizen.main.R;
import motocitizen.utils.DateUtils;

public class VolunteerRow extends TableRow {
    public VolunteerRow(Context context, Volunteer volunteer) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.volunteer_row, this, true);
        ((TextView) this.findViewById(R.id.volunteer)).setText(volunteer.getName());
        ((TextView) this.findViewById(R.id.action)).setText(volunteer.getStatus().string());
        ((TextView) this.findViewById(R.id.time)).setText(DateUtils.getTime(volunteer.getTime()));
    }
}
