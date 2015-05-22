package motocitizen.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import motocitizen.MyApp;
import motocitizen.app.general.Accident;
import motocitizen.startup.MyPreferences;

public class AccidentDetailsFragments extends Fragment {

    protected static final String ACCIDENT_ID = "accidentID";
    protected static final String USER_NAME = "userName";

    protected int accidentID;
    protected String userName;

    protected Accident currentPoint;
    protected MyPreferences prefs;

    int mStackLevel = 0;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mStackLevel = savedInstanceState.getInt("level");
        }

        if (getArguments() != null) {
            accidentID = getArguments().getInt(ACCIDENT_ID);
            userName = getArguments().getString(USER_NAME);
        }
        prefs = ((MyApp) getActivity().getApplicationContext()).getPreferences();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }
}
