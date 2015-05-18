package motocitizen.Activity;

import android.app.Fragment;

import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.startup.MyPreferences;

public class AccidentDetailsFragments extends Fragment {

    protected static final String ACCIDENT_ID = "accidentID";
    protected int accidentID;
    protected Accident currentPoint;
    protected MyPreferences prefs;

    protected void update() {
        currentPoint = AccidentsGeneral.points.getPoint(accidentID);
    }
}
