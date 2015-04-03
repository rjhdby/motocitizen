package motocitizen.app.mc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Toast;

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
//    public static final Button.OnClickListener authConfirmListener = new Button.OnClickListener() {
//        public void onClick(View v) {
//            if (MCAccidents.auth.anonim) {
//                Text.set(R.id.auth_error_helper, "");
//                Show.show(R.id.main_frame_settings);
//            } else {
//                Startup.prefsDef.edit().putString("mc.login", Text.get(R.id.mc_auth_login)).commit();
//                Startup.prefsDef.edit().putString("mc.password", Text.get(R.id.mc_auth_password)).commit();
////                MCAccidents.auth.auth();
//                MCAccidents.auth.setAccess(v.getContext());
//                MCInit.setupAccess(v.getContext(), MCAccidents.auth);
//                MCInit.setupValues(MCAccidents.auth);
//                if (MCAccidents.auth.name.equals("")) {
//                    //Text.set(R.id.value_mcaccidents_auth_name, Startup.prefsDef.getString("mc.name", ""));
//                    Text.set(R.id.auth_error_helper, "Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
//                } else {
//                    Text.set(R.id.auth_error_helper, "");
//                    Show.show(R.id.main_frame_settings);
//                }
//            }
//        }
//    };

    public static final Button.OnClickListener authButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            String login = Startup.prefs.getString("mc_login", "");
            String password = Startup.prefs.getString("mc_password", "");
            Text.set(R.id.mc_auth_login, login);
            Text.set(R.id.mc_auth_password, password);
            Show.show(R.id.mc_auth);
        }
    };

//    public static final Button.OnClickListener authCancelListener = new Button.OnClickListener() {
//        public void onClick(View v) {
//            if (MCAccidents.auth.name.equals("")) {
//                MCAccidents.auth.setAnonim(v.getContext(), true);
//                MCInit.setupAccess(v.getContext(), MCAccidents.auth);
//                MCInit.setupValues(MCAccidents.auth);
//            } else {
//                String login = Startup.prefsDef.getString("mc.login", "");
//                String password = Startup.prefsDef.getString("mc.password", "");
//                Text.set(R.id.mc_auth_login, login);
//                Text.set(R.id.mc_auth_password, password);
//            }
//            Show.show(R.id.main_frame_settings);
//        }
//    };

    public static final Button.OnClickListener dialButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+74957447350"));
            Startup.context.startActivity(intent);
        }
    };

    public static final Button.OnClickListener createAccButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Startup.context.startActivity(new Intent(Startup.context, CreateAccActivity.class));
        }
    };

    public static final Button.OnClickListener newMessageButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            if (Startup.isOnline()) {
                String text = Text.get(R.id.mc_new_message_text);
                int currentId = MCAccidents.currentPoint.id;
                Map<String, String> post = new HashMap<>();
                post.put("login", MCAccidents.auth.getLogin());
                post.put("passhash", MCAccidents.auth.makePassHash());
                post.put("id", String.valueOf(currentId));
                post.put("text", text);
                JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
                if (request != null) {
                    (new SendMessageRequest(currentId)).execute(request);
                }
            } else {
                Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
            }
        }
    };

    public static void parseSendMessageResponse(int currentId) {
        MCAccidents.refresh(Startup.context);
        MCAccidents.toDetails(Startup.context, currentId);
        Text.set(R.id.mc_new_message_text, "");
        Keyboard.hide(((Activity) Startup.context).findViewById(R.id.mc_new_message_text));
    }

    public static final Button.OnClickListener firstloginButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Show.show(R.id.mc_auth);
        }
    };
    public static final Button.OnClickListener anonimButtonListener = new Button.OnClickListener() {
        public void onClick(View v) {
            MCAccidents.auth.setAnonim(v.getContext(), true);
            MCInit.setupAccess(v.getContext(), MCAccidents.auth);
            MCInit.setupValues(MCAccidents.auth);
            Show.show(R.id.main_frame_applications);
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

    public static final Button.OnClickListener onwayButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Startup.isOnline()) {
                int currentId = MCAccidents.currentPoint.id;
                Map<String, String> post = new HashMap<>();
                post.put("login", MCAccidents.auth.getLogin());
                post.put("passhash", MCAccidents.auth.makePassHash());
                post.put("id", String.valueOf(currentId));
                JsonRequest request = new JsonRequest("mcaccidents", "onway", post, "", true);
                if (request != null) {
                    (new OnwayRequest(currentId)).execute(request);
                }

                MCAccidents.onway = currentId;
                MCAccidents.refresh(v.getContext());
                MCAccidents.toDetails(v.getContext(), currentId);
            } else {
                Toast.makeText(Startup.context, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
            }
        }
    };

    public static void parseOnwayResponse(int currentId) {
        MCAccidents.onway = currentId;
        MCAccidents.refresh(Startup.context);
        MCAccidents.toDetails(Startup.context, currentId);
    }

    public static final RadioGroup.OnCheckedChangeListener mainTabsListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();

            MCObjects.accListView.setVisibility(View.VISIBLE);
            MCObjects.accDetailsView.setVisibility(View.VISIBLE);
            MCObjects.mapContainer.setVisibility(View.VISIBLE);

            if (Show.currentGeneral == null) {
                Show.currentGeneral = R.id.tab_accidents_button;
            }

            if (id == R.id.tab_accidents_button) {
                MCObjects.accListView.animate().translationX(0);
                MCObjects.accDetailsView.animate().translationX(Const.width);
                MCObjects.mapContainer.animate().translationX(Const.width * 2);
            } else if (id == R.id.tab_acc_details_button) {
                MCObjects.accListView.animate().translationX(-Const.width);
                MCObjects.accDetailsView.animate().translationX(0);
                MCObjects.mapContainer.animate().translationX(Const.width);
            } else if (id == R.id.tab_map_button) {
                MCObjects.accListView.animate().translationX(-Const.width * 2);
                MCObjects.accDetailsView.animate().translationX(-Const.width);
                MCObjects.mapContainer.animate().translationX(0);
            }
            Show.currentGeneral = id;
        }
    };
    public static final RadioGroup.OnCheckedChangeListener accDetTabsListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            MCObjects.detMessages.setVisibility(View.INVISIBLE);
            MCObjects.detHistory.setVisibility(View.INVISIBLE);
            MCObjects.detVolunteers.setVisibility(View.INVISIBLE);
            if (id == R.id.mc_det_tab_messages) {
                MCObjects.detMessages.setVisibility(View.VISIBLE);
            } else if (id == R.id.mc_det_tab_history) {

                MCObjects.detHistory.setVisibility(View.VISIBLE);
            } else if (id == R.id.mc_det_tab_people) {

                MCObjects.detVolunteers.setVisibility(View.VISIBLE);

            }
        }
    };
    public static final TextWatcher mcNewMessageTextListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String temp = s.toString().replaceAll("\\s", "");
            if (temp.length() == 0) {
                MCObjects.newMessageButton.setEnabled(false);
            } else {
                MCObjects.newMessageButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    };
}
