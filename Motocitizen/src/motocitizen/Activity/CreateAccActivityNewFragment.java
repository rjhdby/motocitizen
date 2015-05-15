package motocitizen.Activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import motocitizen.main.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateAccActivityNewFragment extends Fragment {

    public CreateAccActivityNewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mc_app_create_point, container, false);
    }
}
