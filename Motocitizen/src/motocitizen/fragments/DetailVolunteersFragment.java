package motocitizen.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.content.Content;
import motocitizen.content.accident.Accident;
import motocitizen.content.volunteer.VolunteerAction;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentListRequest;
import motocitizen.network.requests.CancelOnWayRequest;
import motocitizen.network.requests.OnWayRequest;
import motocitizen.rows.RowFactory;
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
    public static DetailVolunteersFragment newInstance(int accID) {
        DetailVolunteersFragment fragment = new DetailVolunteersFragment();
        Bundle                   args     = new Bundle();
        args.putInt(ACCIDENT_ID, accID);
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

//        update();

        return viewMain;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        update();
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
        for (VolunteerAction v : accident.getVolunteers()) {
            vg_onway.addView(RowFactory.INSTANCE.make(getActivity(), v));
        }
    }

    private void setupAccess() {
        Accident accident = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();
        int      id       = accident.getId();
        boolean  active   = accident.isActive() && User.getInstance(getActivity()).isAuthorized();
        onwayButton.setVisibility(id != Preferences.Companion.getInstance(getActivity()).getOnWay() && id != Content.INSTANCE.getInPlace() && active ? View.VISIBLE : View.GONE);
        onwayCancelButton.setVisibility(id == Preferences.Companion.getInstance(getActivity()).getOnWay() && id != Content.INSTANCE.getInPlace() && active ? View.VISIBLE : View.GONE);
        onwayDisabledButton.setVisibility(id == Content.INSTANCE.getInPlace() && active ? View.VISIBLE : View.GONE);
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
                    sendOnWay();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // todo After Cancel code.
                }
                break;
            case DIALOG_CANCEL_ONWAY_CONFIRM:
                if (resultCode == Activity.RESULT_OK) {
                    sendCancelOnWay();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // todo After Cancel code.
                }
                break;
            case DIALOG_ACC_NOT_ACTUAL:
                getActivity().finish();
                break;
        }
    }

    private void sendOnWay() {
        Preferences.Companion.getInstance(getActivity()).setOnWay(accidentID);
        new OnWayRequest(accidentID, response -> new AccidentListRequest(result -> {
            Content.INSTANCE.requestUpdate();
            getActivity().runOnUiThread(() -> {
                ((AccidentDetailsActivity) getActivity()).update();
                update();
            });
        }));
    }

    private void sendCancelOnWay() {
        Preferences.Companion.getInstance(getActivity()).setOnWay(0);
        new CancelOnWayRequest(accidentID, response -> new AccidentListRequest(result -> {
            Content.INSTANCE.requestUpdate();
            getActivity().runOnUiThread(() -> {
                ((AccidentDetailsActivity) getActivity()).update();
                update();
            });
        }));
    }
}
