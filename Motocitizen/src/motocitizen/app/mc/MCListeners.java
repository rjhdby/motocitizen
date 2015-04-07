package motocitizen.app.mc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import motocitizen.Activity.CreateAccActivity;
import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.network.JsonRequest;
import motocitizen.network.OnwayRequest;
import motocitizen.network.SendMessageRequest;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Show;
import motocitizen.utils.Text;

public class MCListeners {

    public static final Button.OnClickListener firstloginButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Show.show(R.id.mc_auth);
        }
    };

    public static final OnCheckedChangeListener authAnonimCheckBoxListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                MCAccidents.auth.setAnonim(buttonView.getContext(), true);
                MCInit.setupAccess(buttonView.getContext(), MCAccidents.auth);
                MCInit.setupValues(MCAccidents.auth);
            } else {
                MCAccidents.auth.setAnonim(buttonView.getContext(), false);
                MCInit.setupAccess(buttonView.getContext(), MCAccidents.auth);
                MCInit.setupValues(MCAccidents.auth);
            }
        }
    };
//    public static final Button.OnClickListener selectSoundButtonListener = new Button.OnClickListener() {
//        public void onClick(View v) {
//            Show.show(R.id.mc_select_sound_screen);
//            new MCSelectSound(v.getContext());
//        }
//    };

//    public static final RadioGroup.OnCheckedChangeListener mainTabsListener = new RadioGroup.OnCheckedChangeListener() {
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            int id = group.getCheckedRadioButtonId();
//
//            MCObjects.accListView.setVisibility(View.VISIBLE);
//            MCObjects.mapContainer.setVisibility(View.VISIBLE);
//
//            if (Show.currentGeneral == null) {
//                Show.currentGeneral = R.id.tab_accidents_button;
//            }
//
//            if (id == R.id.tab_accidents_button) {
//                MCObjects.accListView.animate().translationX(0);
//                MCObjects.mapContainer.animate().translationX(Const.width * 2);
//            } else if (id == R.id.tab_map_button) {
//                MCObjects.accListView.animate().translationX(-Const.width * 2);
//                MCObjects.mapContainer.animate().translationX(0);
//            }
//            Show.currentGeneral = id;
//        }
//    };
}
