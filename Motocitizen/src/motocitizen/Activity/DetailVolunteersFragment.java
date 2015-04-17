package motocitizen.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCVolunteer;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.network.OnwayRequest;
import motocitizen.startup.Startup;

import static motocitizen.app.mc.MCAccidents.getDelimiterRow;

public class DetailVolunteersFragment extends AccidentDetailsFragments {

    private OnFragmentInteractionListener mListener;

    private Button onwayButton;
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
        if (getArguments() != null) {
            accidentID = getArguments().getInt(ACCIDENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_volunteers, container, false);

        onwayButton = (Button) viewMain.findViewById(R.id.onway_button);
        onwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Startup.isOnline()) {
                    int currentId = MCAccidents.getCurrentPointID();
                    Map<String, String> post = new HashMap<>();
                    post.put("login", MCAccidents.auth.getLogin());
                    post.put("passhash", MCAccidents.auth.makePassHash());
                    post.put("id", String.valueOf(currentId));
                    JsonRequest request = new JsonRequest("mcaccidents", "onway", post, "", true);
                    if (request != null) {
                        (new OnwayRequest((AccidentDetailsActivity) getActivity(), currentId)).execute(request);
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
                }
            }
        });

        onwayContent = viewMain.findViewById(R.id.acc_onway_table);
        inplaceContent = viewMain.findViewById(R.id.acc_inplace_table);
        toMap = (Button) viewMain.findViewById(R.id.details_to_map_button);
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
        // TODO: Update argument type and name
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
            MCVolunteer current = currentPoint.volunteers.get(i);
            if (current.status.equals("onway")) {
                if (vg_onway.getVisibility() == View.INVISIBLE) {
                    vg_onway.setVisibility(View.VISIBLE);
                    vg_onway.addView(getDelimiterRow(getActivity(), "В пути"));
                }
                vg_onway.addView(current.createRow(getActivity()));
            }
        }
    }

    public void notifyDataSetChanged() {
        update();
//  ListAdapter
//        adapter.notifyDataSetChanged();
    }

    public void setupAccess() {
        if (prefs == null) {
            prefs = ((AccidentDetailsActivity) getActivity()).getPref();
        }
        if (currentPoint.getId() == prefs.getOnWay() || currentPoint.getId() == MCAccidents.getInplaceID() || !MCAccidents.auth.isAuthorized()) {
            onwayButton.setVisibility(View.INVISIBLE);
        } else {
            onwayButton.setVisibility(View.VISIBLE);
        }
    }
}
