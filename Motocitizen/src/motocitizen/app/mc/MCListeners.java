package motocitizen.app.mc;

import java.util.HashMap;
import java.util.Map;

import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Const;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Show;
import motocitizen.utils.Text;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;

public class MCListeners {
	public static Button.OnClickListener authConfirmListener = new Button.OnClickListener() {
		public void onClick(View v) {
			if (MCAccidents.auth.anonim) {
				Text.set(R.id.auth_error_helper, "");
				Show.show(R.id.main_frame_settings);
			} else {
				Startup.prefs.edit().putString("mc.login", Text.get(R.id.mc_auth_login)).commit();
				Startup.prefs.edit().putString("mc.password", Text.get(R.id.mc_auth_password)).commit();
				MCAccidents.auth.auth();
				MCAccidents.auth.setAccess();
				MCInit.setupAccess(MCAccidents.auth);
				MCInit.setupValues(MCAccidents.auth);
				if (MCAccidents.auth.name.equals("")) {
					Text.set(R.id.value_mcaccidents_auth_name, Startup.prefs.getString("mc.name", ""));
					Text.set(R.id.auth_error_helper, "Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
				} else {
					Text.set(R.id.auth_error_helper, "");
					Show.show(R.id.main_frame_settings);
				}
			}
		}
	};
	public static Button.OnClickListener authButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String login = Startup.prefs.getString("mc_login", "");
			String password = Startup.prefs.getString("mc_password", "");
			Text.set(R.id.mc_auth_login, login);
			Text.set(R.id.mc_auth_password, password);
			Show.show(R.id.mc_auth);
		}
	};
	public static Button.OnClickListener authCancelListener = new Button.OnClickListener() {
		public void onClick(View v) {
			if (MCAccidents.auth.name.equals("")) {
				MCAccidents.auth.setAnonim(true);
				MCInit.setupAccess(MCAccidents.auth);
				MCInit.setupValues(MCAccidents.auth);
			} else {
				String login = Startup.prefs.getString("mc.login", "");
				String password = Startup.prefs.getString("mc.password", "");
				Text.set(R.id.mc_auth_login, login);
				Text.set(R.id.mc_auth_password, password);
			}
			Show.show(R.id.main_frame_settings);
		}
	};
	public static Button.OnClickListener dialButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:+74957447350"));
			Startup.context.startActivity(intent);
		}
	};
	public static Button.OnClickListener createAccButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Show.show(R.id.mc_create_main);
			MCCreateAcc.init();
		}
	};
	public static Button.OnClickListener newMessageButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String text = Text.get(R.id.mc_new_message_text);
			int currentId = MCAccidents.currentPoint.id;
			Map<String, String> post = new HashMap<String, String>();
			post.put("login", MCAccidents.auth.getLogin());
			post.put("passhash", MCAccidents.auth.makePassHash());
			post.put("id", String.valueOf(currentId));
			post.put("text", text);
			new JSONCall("mcaccidents", "message").request(post);
			MCAccidents.refresh();
			MCAccidents.toDetails(currentId);
			Text.set(R.id.mc_new_message_text, "");
			Keyboard.hide(((Activity) Startup.context).findViewById(R.id.mc_new_message_text));
		}
	};
	public static Button.OnClickListener firstloginButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Show.show(R.id.mc_auth);
		}
	};
	public static Button.OnClickListener anonimButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			MCAccidents.auth.setAnonim(true);
			MCInit.setupAccess(MCAccidents.auth);
			MCInit.setupValues(MCAccidents.auth);
			Show.show(R.id.main_frame_applications);
		}
	};
	public static OnCheckedChangeListener authAnonimCheckBoxListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				MCAccidents.auth.setAnonim(true);
				MCInit.setupAccess(MCAccidents.auth);
				MCInit.setupValues(MCAccidents.auth);
			} else {
				MCAccidents.auth.setAnonim(false);
				MCInit.setupAccess(MCAccidents.auth);
				MCInit.setupValues(MCAccidents.auth);
			}
		}
	};
	public static Button.OnClickListener selectSoundButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Show.show(R.id.mc_select_sound_screen);
			new MCSelectSound();
		}
	};

	public static Button.OnClickListener selectSoundConfirmListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (MCSelectSound.currentId != 0) {
				Startup.prefs.edit().putString("mc.notification.sound", MCSelectSound.sounds.get(MCSelectSound.currentId).getAbsolutePath()).commit();
			}
			Show.show(R.id.main_frame_settings);
		}

	};
	public static Button.OnClickListener selectSoundCancelButton = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Show.show(R.id.main_frame_settings);
		}
	};
	
	public static Button.OnClickListener onwayButtonListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			int currentId = MCAccidents.currentPoint.id;
			Map<String, String> post = new HashMap<String, String>();
			post.put("login", MCAccidents.auth.getLogin());
			post.put("passhash", MCAccidents.auth.makePassHash());
			post.put("id", String.valueOf(currentId));
			new JSONCall("mcaccidents", "onway").request(post);
			MCAccidents.onway = currentId;
			MCAccidents.refresh();
			MCAccidents.toDetails(currentId);
		}
	};
	
	public static RadioGroup.OnCheckedChangeListener mainTabsListener = new RadioGroup.OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int id = group.getCheckedRadioButtonId();
			MCObjects.accListView.setVisibility(View.INVISIBLE);
			MCObjects.accDetailsView.setVisibility(View.INVISIBLE);
			MCObjects.osmMapContainer.setVisibility(View.INVISIBLE);
			switch(id){
			case R.id.tab_accidents_button:
				MCObjects.accListView.setVisibility(View.VISIBLE);
				break;
			case R.id.tab_acc_details_button:
				MCObjects.accDetailsView.setVisibility(View.VISIBLE);
				break;
			case R.id.tab_map_button:
				MCObjects.osmMapContainer.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
	public static RadioGroup.OnCheckedChangeListener accDetTabsListener = new RadioGroup.OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int id = group.getCheckedRadioButtonId();
			MCObjects.detMessages.setVisibility(View.INVISIBLE);
			MCObjects.detHistory.setVisibility(View.INVISIBLE);
			MCObjects.detVolunteers.setVisibility(View.INVISIBLE);
			switch(id){
			case R.id.mc_det_tab_messages:
				MCObjects.detMessages.setVisibility(View.VISIBLE);
				break;
			case R.id.mc_det_tab_history:
				MCObjects.detHistory.setVisibility(View.VISIBLE);
				break;
			case R.id.mc_det_tab_people:
				MCObjects.detVolunteers.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
}
