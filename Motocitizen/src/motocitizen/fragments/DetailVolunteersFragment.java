package motocitizen.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.content.accident.Accident;
import motocitizen.content.volunteer.Volunteer;
import motocitizen.dictionary.Content;
import motocitizen.dictionary.VolunteerStatus;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.CancelOnWayRequest;
import motocitizen.network.requests.OnWayRequest;
import motocitizen.network2.requests.AccidentListRequest;
import motocitizen.rows.details.VolunteerRow;
import motocitizen.user.User;
import motocitizen.utils.Preferences;

public class DetailVolunteersFragment extends AccidentDetailsFragments {

    private static final int DIALOG_ONWAY_CONFIRM        = 1;
    private static final int DIALOG_ACC_NOT_ACTUAL       = 2;
    private static final int DIALOG_CANCEL_ONWAY_CONFIRM = 3;

    private ImageButton onwayButton;
    private ImageButton onwayCancelButton;
    private ImageButton onwayDisabledButton;
    private View        onwayContent;

    // TODO: Rename and change types and number of parameters
    public static DetailVolunteersFragment newInstance(int accID, String userName) {
        DetailVolunteersFragment fragment = new DetailVolunteersFragment();
        Bundle                   args     = new Bundle();
        args.putInt(ACCIDENT_ID, accID);
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailVolunteersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_volunteers, container, false);

        View toMap = viewMain.findViewById(R.id.details_to_map_button);
        onwayButton = (ImageButton) viewMain.findViewById(R.id.onway_button);
        onwayCancelButton = (ImageButton) viewMain.findViewById(R.id.onway_cancel_button);
        onwayDisabledButton = (ImageButton) viewMain.findViewById(R.id.onway_disabled_button);
        onwayContent = viewMain.findViewById(R.id.acc_onway_table);

        onwayDisabledButton.setEnabled(false);

        onwayButton.setOnClickListener(v -> showDialog(DIALOG_ONWAY_CONFIRM));
        onwayCancelButton.setOnClickListener(v -> showDialog(DIALOG_CANCEL_ONWAY_CONFIRM));
        toMap.setOnClickListener(v -> ((AccidentDetailsActivity) getActivity()).jumpToMap());

        update();

        return viewMain;
    }

    private void update() {
        Accident accident = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();

        if (accident == null) {
            showDialog(DIALOG_ACC_NOT_ACTUAL);
            return;
        }

        setupAccess();
        ViewGroup vg_onway = (ViewGroup) onwayContent;
        vg_onway.removeAllViews();
        for (int i : accident.getVolunteers().keySet()) {
            Volunteer current = accident.getVolunteer(i);
            if (current.getStatus() == VolunteerStatus.LEAVE) continue;
            vg_onway.addView(new VolunteerRow(getActivity(), current));
        }
    }

    private void setupAccess() {
        Accident accident = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();
        int      id       = accident.getId();
        boolean  active   = accident.isActive() && User.getInstance(getActivity()).isAuthorized();
        onwayButton.setVisibility(id != Preferences.Companion.getInstance(getActivity()).getOnWay() && id != Content.getInstance().getInPlaceId() && active ? View.VISIBLE : View.GONE);
        onwayCancelButton.setVisibility(id == Preferences.Companion.getInstance(getActivity()).getOnWay() && id != Content.getInstance().getInPlaceId() && active ? View.VISIBLE : View.GONE);
        onwayDisabledButton.setVisibility(id == Content.getInstance().getInPlaceId() && active ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("CommitTransaction")
    private void showDialog(int type) {
        mStackLevel++;
        FragmentTransaction ft   = getActivity().getFragmentManager().beginTransaction();
        Fragment            prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();
        Activity act = getActivity();

        switch (type) {
            case DIALOG_ONWAY_CONFIRM:
                DialogFragment onwayConfirm = ConfirmDialog.newInstance(act.getString(R.string.title_dialog_onway_confirm), act.getString(android.R.string.yes), act.getString(android.R.string.no));
                onwayConfirm.setTargetFragment(this, type);
                onwayConfirm.show(getFragmentManager().beginTransaction(), "dialog");
                break;

            case DIALOG_CANCEL_ONWAY_CONFIRM:
                DialogFragment cancelOnwayConfirm = ConfirmDialog.newInstance(act.getString(R.string.title_dialog_cancel_onway_confirm),
                                                                              act.getString(android.R.string.yes),
                                                                              act.getString(android.R.string.no));
                cancelOnwayConfirm.setTargetFragment(this, type);
                cancelOnwayConfirm.show(getFragmentManager().beginTransaction(), "dialog");
                break;

            case DIALOG_ACC_NOT_ACTUAL:
                DialogFragment dialogFrag = ConfirmDialog.newInstance(act.getString(R.string.title_dialog_acc_not_actual), act.getString(android.R.string.ok), "");
                dialogFrag.setTargetFragment(this, type);
                dialogFrag.show(getFragmentManager().beginTransaction(), "dialog");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DIALOG_ONWAY_CONFIRM:
                if (resultCode == Activity.RESULT_OK) {
                    sendOnway();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // todo After Cancel code.
                }
                break;
            case DIALOG_CANCEL_ONWAY_CONFIRM:
                if (resultCode == Activity.RESULT_OK) {
                    sendCancelOnway();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // todo After Cancel code.
                }
                break;
            case DIALOG_ACC_NOT_ACTUAL:
                getActivity().finish();
                break;
        }
    }

    private void sendOnway() {
        Preferences.Companion.getInstance(getActivity()).setOnWay(accidentID);
        new OnWayRequest(new OnWayCallback(), accidentID);
    }

    private void message(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    private class OnWayCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    message(result.getString("error"));
                } catch (JSONException e) {
                    message("Неизвестная ошибка " + result.toString());
                }
            } else {
                new AccidentListRequest(result1 -> {
                    Content.getInstance().requestUpdate();
                    getActivity().runOnUiThread(() -> {
                        ((AccidentDetailsActivity) getActivity()).update();
                        update();
                    });
                });
            }
        }
    }

    private void sendCancelOnway() {
        Preferences.Companion.getInstance(getActivity()).setOnWay(0);
        new CancelOnWayRequest(new OnWayCallback(), accidentID);
    }
}
