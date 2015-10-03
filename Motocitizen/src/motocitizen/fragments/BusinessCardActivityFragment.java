package motocitizen.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import motocitizen.main.R;

public class BusinessCardActivityFragment extends Fragment {

    public BusinessCardActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_business_card, container, false);
        ImageView imageViewQrForum = (ImageView) viewMain.findViewById(R.id.imageViewQrForum);
        imageViewQrForum.setImageResource(R.drawable.qr_forum);
        return viewMain;
    }
}
