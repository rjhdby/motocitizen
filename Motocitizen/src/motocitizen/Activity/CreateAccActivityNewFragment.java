package motocitizen.Activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import motocitizen.main.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateAccActivityNewFragment extends Fragment implements View.OnClickListener {

    private Button cancel;

    public CreateAccActivityNewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View viewMain = inflater.inflate(R.layout.mc_app_create_point, container, false);
        View viewMain = inflater.inflate(R.layout.mc_app_create_point, container, false);

        cancel = (Button) viewMain.findViewById(R.id.mc_create_cancel);
        cancel.setOnClickListener(this);

        return viewMain;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.mc_create_cancel:
                getActivity().finish();
                break;
        }
    }
}
