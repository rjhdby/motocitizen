package motocitizen.Activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.general.AccidentVolunteer;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.network.OnwayRequest;
import motocitizen.startup.Startup;

import static motocitizen.app.general.AccidentsGeneral.getDelimiterRow;

public class DetailVolunteersFragment extends AccidentDetailsFragments {

    int mStackLevel = 0;
    public static final int DIALOG_ONWAY_CONFIRM = 1;

    private OnFragmentInteractionListener mListener;

    private ImageButton onwayButton;
    private View toMap;
    private View onwayContent;
    private View inplaceContent;

    // TODO: Rename and change types and number of parameters
    public static DetailVolunteersFragment newInstance(int param1) {
        DetailVolunteersFragment fragment = new DetailVolunteersFragment();
        Bundle args = new Bundle();
        args.putInt(ACCIDENT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailVolunteersFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_volunteers, container, false);

        onwayButton = (ImageButton) viewMain.findViewById(R.id.onway_button);
        onwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ONWAY_CONFIRM);
            }
        });

        onwayContent = viewMain.findViewById(R.id.acc_onway_table);
        inplaceContent = viewMain.findViewById(R.id.acc_inplace_table);
        toMap = viewMain.findViewById(R.id.details_to_map_button);
        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AccidentDetailsActivity) getActivity()).jumpToMap();
            }
        });
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
        public void onFragmentInteraction(Uri uri);
    }

    protected void update() {
        super.update();
        setupAccess();

        ViewGroup vg_onway = (ViewGroup) onwayContent;
        ViewGroup vg_inplace = (ViewGroup) inplaceContent;
        vg_onway.setVisibility(View.INVISIBLE);
        vg_inplace.setVisibility(View.INVISIBLE);
        vg_onway.removeAllViews();
        vg_inplace.removeAllViews();
        for (int i : currentPoint.getSortedVolunteersKeys()) {
            AccidentVolunteer current = currentPoint.volunteers.get(i);
            switch (current.getStatus()) {
                case ONWAY:
                    if (vg_onway.getVisibility() == View.INVISIBLE) {
                        vg_onway.setVisibility(View.VISIBLE);
                        vg_onway.addView(getDelimiterRow(getActivity(), "В пути"));
                    }
                    vg_onway.addView(current.createRow(getActivity()));
                    break;
                case INPLACE:
                    if (vg_onway.getVisibility() == View.INVISIBLE) {
                        vg_onway.setVisibility(View.VISIBLE);
                        vg_onway.addView(getDelimiterRow(getActivity(), "На месте"));
                    }
                    vg_onway.addView(current.createRow(getActivity()));
                    break;
                case LEAVE:
                    if (vg_onway.getVisibility() == View.INVISIBLE) {
                        vg_onway.setVisibility(View.VISIBLE);
                        vg_onway.addView(getDelimiterRow(getActivity(), "Были"));
                    }
                    vg_onway.addView(current.createRow(getActivity()));
                    break;
            }
        }
    }

    public void notifyDataSetChanged() {
        update();
//  ListAdapter
//        adapter.notifyDataSetChanged();
    }

    private void setupAccess() {
        if (prefs == null) {
            prefs = ((AccidentDetailsActivity) getActivity()).getPref();
        }

        if (prefs == null)
            throw new NullPointerException("prefs == null");
        if (currentPoint == null)
            throw new NullPointerException("currentPoint == null");

        if (currentPoint.getId() == prefs.getOnWay() || currentPoint.getId() == AccidentsGeneral.getInplaceID() || !AccidentsGeneral.auth.isAuthorized() || !currentPoint.isActive()) {
            onwayButton.setVisibility(View.INVISIBLE);
        } else {
            onwayButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("level", mStackLevel);
    }

    void showDialog(int type) {
        mStackLevel++;
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();
        switch (type) {
            case DIALOG_ONWAY_CONFIRM:
                DialogFragment dialogFrag = ConfirmDialog.newInstance(getActivity().getString(R.string.onway_title_confirm));
                dialogFrag.setTargetFragment(this, DIALOG_ONWAY_CONFIRM);
                dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case DIALOG_ONWAY_CONFIRM:
                if (resultCode == Activity.RESULT_OK) {
                    sendOnway();
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                }
                break;
        }
    }

    private void sendOnway() {
        if (Startup.isOnline()) {
            int currentId = AccidentsGeneral.getCurrentPointID();
            AccidentsGeneral.setOnWay(currentId);
            Map<String, String> post = new HashMap<>();
            post.put("login", AccidentsGeneral.auth.getLogin());
            post.put("passhash", AccidentsGeneral.auth.makePassHash());
            post.put("id", String.valueOf(currentId));
            JsonRequest request = new JsonRequest("mcaccidents", "onway", post, "", true);
            if (request != null) {
                (new OnwayRequest((AccidentDetailsActivity) getActivity(), currentId)).execute(request);
            }
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
        }
    }
}
