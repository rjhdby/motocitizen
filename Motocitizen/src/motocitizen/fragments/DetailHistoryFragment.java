package motocitizen.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.draw.Rows;
import motocitizen.main.R;

public class DetailHistoryFragment extends AccidentDetailsFragments {

    private View mcDetLogContent;

    public static DetailHistoryFragment newInstance(int accID, String userName) {
        DetailHistoryFragment fragment = new DetailHistoryFragment();
        Bundle                args     = new Bundle();
        args.putInt(ACCIDENT_ID, accID);
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_history, container, false);
        mcDetLogContent = viewMain.findViewById(R.id.mc_det_log_content);

        update();
        return viewMain;
    }

    private void update() {
        motocitizen.accident.Accident accident = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();

        ViewGroup logView = (ViewGroup) mcDetLogContent;
        logView.removeAllViews();
        for (int i : accident.getHistory().keySet()) {
            logView.addView(Rows.getHistoryRow(logView, accident.getHistory().get(i)));
        }
    }

    public void notifyDataSetChanged() {
        update();
    }
}
