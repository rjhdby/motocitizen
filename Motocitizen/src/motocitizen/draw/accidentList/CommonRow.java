package motocitizen.draw.accidentList;

import android.content.Context;
import android.view.ViewGroup;

import motocitizen.accident.Accident;
import motocitizen.main.R;

public class CommonRow extends Row {
    public CommonRow(Context context, Accident accident, ViewGroup parent) {
        super(context, R.layout.accident_row, accident, parent);
    }

    @Override
    public void makeHidden() {
        makeHidden(R.drawable.accident_row_hidden);
    }

    @Override
    public void makeEnded() {
        makeEnded(R.drawable.accident_row_ended);
    }
}
