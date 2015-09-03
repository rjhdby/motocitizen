package motocitizen.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import motocitizen.main.R;

public class BusinessCardActivityFragment extends Fragment {

    ImageView imageViewQrForum;

    public BusinessCardActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_business_card, container, false);
        imageViewQrForum = (ImageView)viewMain.findViewById(R.id.imageViewQrForum);
        imageViewQrForum.setImageResource(R.drawable.qr_forum);
        return viewMain;
    }
}
