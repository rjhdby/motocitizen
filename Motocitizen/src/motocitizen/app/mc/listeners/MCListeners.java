package motocitizen.app.mc.listeners;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCAccidentsInit;
import motocitizen.app.mc.MCSelectSound;
import motocitizen.app.mc.create.MCCreateAcc;
import motocitizen.app.mc.notification.MCNotification;
import motocitizen.app.mc.user.MCAuth;
import motocitizen.main.R;
import motocitizen.network.JSONCall;
import motocitizen.startup.Startup;
import motocitizen.utils.Keyboard;
import motocitizen.utils.Show;
import motocitizen.utils.Text;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MCListeners {
	public static Button.OnClickListener authConfirmListener = new Button.OnClickListener() {
		public void onClick(View v) {
			if (MCAuth.isAnonimus()) {
				Text.set(R.id.value_mcaccidents_auth_name, "");
				Startup.prefs.edit().putString("mc.name", "").commit();
				Text.set(R.id.auth_error_helper, "");
				MCAccidentsInit.exit();
			} else {
				Startup.prefs.edit().putString("mc.login", Text.get(R.id.mc_auth_login)).commit();
				Startup.prefs.edit().putString("mc.password", Text.get(R.id.mc_auth_password)).commit();
				MCAuth.setAccess();
				if (MCAuth.user.get("name").equals("")) {
					Text.set(R.id.value_mcaccidents_auth_name, Startup.prefs.getString("mc.name", ""));
					Text.set(R.id.auth_error_helper, "Не удалось авторизоваться. Возможно неверно введен логин или пароль.");
				} else {
					Text.set(R.id.auth_error_helper, "");
					MCAccidentsInit.exit();
				}
			}
		}
	};
	public static Button.OnClickListener authButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			MCAccidentsInit.previsionBack = Startup.prefs.getString("backButton", "");
			Startup.prefs.edit().putString("backButton", "motocitizen.app.mc.MCAccidentsInit").commit();
			String login = Startup.prefs.getString("mc_login", "");
			String password = Startup.prefs.getString("mc_password", "");
			Text.set(R.id.mc_auth_login, login);
			Text.set(R.id.mc_auth_password, password);
			Show.show(R.id.mc_auth);
		}
	};
	public static Button.OnClickListener authCancelListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String login = Startup.prefs.getString("mc.login", "");
			String password = Startup.prefs.getString("mc.password", "");
			Text.set(R.id.mc_auth_login, login);
			Text.set(R.id.mc_auth_password, password);
			MCAccidentsInit.exit();
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
			new MCCreateAcc();
		}
	};
	public static Button.OnClickListener newMessageButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String text = Text.get(R.id.mc_new_message_text);
			int currentId = MCAccidents.currentPoint.id;
			Map<String, String> post = new HashMap<String, String>();
			post.put("login", MCAuth.getLogin());
			post.put("passhash", MCAuth.getPassHash());
			post.put("id", String.valueOf(currentId));
			post.put("text", text);
			new JSONCall("mcaccidents", "message").request(post);
			MCAccidents.refresh();
			MCAccidents.toDetails(currentId);
			Text.set(R.id.mc_new_message_text, "");
			Keyboard.hide(((Activity) Startup.context).findViewById(R.id.mc_new_message_text));
			//------------------------------------
			new MCNotification("text");
			//------------------------------------
		}
	};
	public static Button.OnClickListener firstloginButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Show.show(R.id.mc_auth);
		}
	};
	public static Button.OnClickListener anonimButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Startup.prefs.edit().putBoolean("mc.anonim", true).commit();
			MCAuth.setAccess();
			Show.show(R.id.main_frame_applications);
			Show.last = ((Activity) Startup.context).findViewById(R.id.main_frame_applications).getId();
			Show.lastParent = ((Activity) Startup.context).findViewById(R.id.main_frame).getId();
		}
	};
	public static OnCheckedChangeListener authAnonimCheckBoxListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				Startup.prefs.edit().putBoolean("mc.anonim", true).commit();
				((Activity) Startup.context).findViewById(R.id.mc_auth_login).setVisibility(View.INVISIBLE);
				((Activity) Startup.context).findViewById(R.id.mc_auth_password).setVisibility(View.INVISIBLE);
			} else {
				Startup.prefs.edit().putBoolean("mc.anonim", false).commit();
				((Activity) Startup.context).findViewById(R.id.mc_auth_login).setVisibility(View.VISIBLE);
				((Activity) Startup.context).findViewById(R.id.mc_auth_password).setVisibility(View.VISIBLE);
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
			Show.showLast();
		}

	};
	public static Button.OnClickListener cancel = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Show.showLast();
		}

	};
}
