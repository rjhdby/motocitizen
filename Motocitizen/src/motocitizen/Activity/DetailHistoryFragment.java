package motocitizen.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCPointHistory;
import motocitizen.main.R;

public class DetailHistoryFragment extends AccidentDetailsFragments {

    private OnFragmentInteractionListener mListener;

    private View mcDetLogContent;

    public static DetailHistoryFragment newInstance(int param1) {
        DetailHistoryFragment fragment = new DetailHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ACCIDENT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accidentID = getArguments().getInt(ACCIDENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            accidentID = getArguments().getInt(ACCIDENT_ID);
        }

        View viewMain = inflater.inflate(R.layout.fragment_detail_history, container, false);
        mcDetLogContent = viewMain.findViewById(R.id.mc_det_log_content);

        update();
        return viewMain;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    protected void update() {
        super.update();
        currentPoint = MCAccidents.points.getPoint(accidentID);
        ViewGroup logView = (ViewGroup) mcDetLogContent;
        logView.removeAllViews();
        logView.addView(MCPointHistory.createHeader(getActivity()));
        for (int i : currentPoint.getSortedHistoryKeys()) {
            logView.addView(currentPoint.history.get(i).createRow(getActivity()));
        }
    }
}
