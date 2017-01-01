package motocitizen.fragments;

import android.app.Fragment;
import android.os.Bundle;

public class AccidentDetailsFragments extends Fragment {

    /* constants */
    static final String ACCIDENT_ID = "accidentID";
    static final String USER_NAME   = "userName";
    /* end constants */

    int accidentID;
    int mStackLevel;

    {
        mStackLevel = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mStackLevel = savedInstanceState.getInt("level");
        }

        if (getArguments() != null) {
            accidentID = getArguments().getInt(ACCIDENT_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }
}
