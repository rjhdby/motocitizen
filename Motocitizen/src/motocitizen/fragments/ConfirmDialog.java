package motocitizen.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ConfirmDialog extends DialogFragment {

    public static ConfirmDialog newInstance(String title, String positiveButton, String negativeButton) {
        ConfirmDialog dialogFragment = new ConfirmDialog();
        Bundle        bundle         = new Bundle();
        bundle.putString("title", title);
        bundle.putString("positive", positiveButton);
        bundle.putString("negative", negativeButton);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder res = new AlertDialog.Builder(getActivity());
        res.setTitle(getArguments().getString("title"));
        res.setIcon(android.R.drawable.ic_dialog_alert);
        String positive = getArguments().getString("positive");
        String negative = getArguments().getString("negative");
        if (positive != null && !positive.isEmpty())
            res.setPositiveButton(positive, (dialog, whichButton) -> getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent()));
        if (negative != null && !negative.isEmpty())
            res.setNegativeButton(negative, (dialog, whichButton) -> getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent()));
        return res.create();
    }
}
