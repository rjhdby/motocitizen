package motocitizen.Activity;

import android.app.Fragment;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPoint;
import motocitizen.startup.MCPreferences;

/**
 * Created by pavel on 12.04.15.
 */
public class AccidentDetailsFragments extends Fragment {

    protected static final String ACCIDENT_ID = "accidentID";
    protected int accidentID;
    protected MCPoint currentPoint;
    protected MCPreferences prefs;

    protected void update() {
        currentPoint = MCAccidents.points.getPoint(accidentID);
        prefs = ((AccidentDetailsActivity)getActivity()).getPref();
    }
}
