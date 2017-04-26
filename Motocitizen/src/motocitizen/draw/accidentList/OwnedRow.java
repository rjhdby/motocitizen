package motocitizen.draw.accidentList;

import android.content.Context;
import android.view.ViewGroup;

import motocitizen.accident.Accident;
import motocitizen.main.R;

public class OwnedRow extends Row {
    public OwnedRow(Context context, Accident accident, ViewGroup parent) {
        super(context, R.layout.accident_row_i_was_here, accident, parent);
    }

    @Override
    public void makeHidden() {
        makeHidden(R.drawable.owner_accident_hidden);
    }

    @Override
    public void makeEnded() {
        makeEnded(R.drawable.owner_accident_ended);
    }
}
